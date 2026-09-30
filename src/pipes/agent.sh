#!/bin/bash
# URL 등록 에이전트(qqueueing-agent 컨테이너의 진입점)다.
# - main과 공유하는 볼륨에 FIFO(/pipes/pipe)를 만들고 계속 읽는다.
# - main은 FIFO에 "bash <shell.fileNm> register|delete <url>" 한 줄을 쓴다. 이 형식과 URL 규칙에 맞는 줄만
#   conf.sh에 인자로 넘기고, 그 밖의 입력은 실행하지 않고 로그만 남긴다. 입력을 셸 코드로 실행(eval)하지 않는다.
# - 대상 nginx 컨테이너가 실행 중이면 초기 설정(conf.sh init)을 넣는다. 컨테이너가 재생성·재시작되면 다시 확인한다.
set -u

AGENT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# main의 shell.path(/pipes) 아래 pipe 파일이다(ScriptExecService가 "> pipe"로 쓴다).
PIPE=/pipes/pipe
TARGET="${TARGET_NGINX_CONTAINER:-demo-nginx}"
# FIFO 입력이 없을 때 이 간격(초)으로 깨어나 대상 컨테이너 상태를 확인한다.
CHECK_INTERVAL=5
# main의 shell.fileNm(src/main/src/main/resources/application.yml)과 같아야 한다.
COMMAND_FILE=conf.sh
# http(s)://호스트/경로 형식만 받는다. URL은 nginx 설정(proxy_set_header Target-URL <url>)에 그대로 들어가므로
# ; { } 공백 따옴표 $ # \ 같은 문자는 받지 않는다. register.py가 호스트를 server_name과 그대로 비교하므로 포트도 받지 않는다.
URL_RE='^https?://[A-Za-z0-9.-]+/[A-Za-z0-9._~/%+,:@=&?-]*$'

# 초기 설정을 마친 대상 컨테이너의 "ID 시작시각 실행여부". 컨테이너가 바뀌면 값이 달라져서 다시 초기화한다.
initialized_for=""
last_state=""

log() {
	printf '%s [agent] %s\n' "$(date '+%F %T')" "$*"
}

trap 'log "종료 신호를 받아 멈춘다."; exit 0' TERM INT

prepare_pipe() {
	mkdir -p "$(dirname "$PIPE")"
	if [[ -e $PIPE && ! -p $PIPE ]]; then
		log "$PIPE 가 FIFO가 아니라서 지우고 다시 만든다."
		rm -f "$PIPE"
	fi
	if [[ ! -p $PIPE ]]; then
		mkfifo -m 600 "$PIPE"
	fi
}

# 대상 컨테이너가 실행 중이고 이 컨테이너(ID·시작시각)에 아직 초기 설정을 확인하지 않았으면 conf.sh init을 실행한다.
ensure_init() {
	local state
	state=$(docker inspect --type container -f '{{.Id}} {{.State.StartedAt}} {{.State.Running}}' "$TARGET" 2>/dev/null) || state="missing"
	if [[ $state != *" true" ]]; then
		if [[ $state != "$last_state" ]]; then
			log "대상 nginx 컨테이너 $TARGET 가 실행 중이 아니다. 뜨면 초기 설정을 넣는다."
		fi
		last_state=$state
		return
	fi
	last_state=$state
	if [[ $state == "$initialized_for" ]]; then
		return
	fi
	log "$TARGET 의 초기 설정을 확인한다."
	if bash "$AGENT_DIR/conf.sh" init; then
		initialized_for=$state
		log "$TARGET 초기 설정 완료"
	else
		log "$TARGET 초기 설정 실패. ${CHECK_INTERVAL}초 뒤 다시 시도한다."
	fi
}

handle_line() {
	local line=$1 shown bin file action url rest
	printf -v shown '%q' "$line"
	read -r bin file action url rest <<<"$line"
	if [[ $bin != bash || $file != "$COMMAND_FILE" || ! $action =~ ^(register|delete)$ || -n $rest || ! $url =~ $URL_RE ]]; then
		log "형식에 맞지 않는 입력이라 실행하지 않고 버린다: $shown"
		return
	fi
	log "$action $url 시작"
	if bash "$AGENT_DIR/conf.sh" "$action" "$url"; then
		log "$action $url 완료"
	else
		log "$action $url 실패"
	fi
}

if ! docker version >/dev/null 2>&1; then
	log "Docker 데몬에 연결하지 못했다. /var/run/docker.sock 마운트를 확인한다."
	exit 1
fi

prepare_pipe
# 읽기·쓰기로 열어 둔다. 그러면 main이 쓸 때 open에서 기다리지 않고(처리 중인 요청이 있으면 파이프 버퍼에 쌓인다),
# main이 파일을 닫아도 EOF가 오지 않는다.
exec 3<>"$PIPE"
log "FIFO $PIPE 를 열었다. 대상 nginx 컨테이너: $TARGET"

while true; do
	ensure_init
	if IFS= read -r -t "$CHECK_INTERVAL" line <&3; then
		handle_line "$line"
	fi
done
