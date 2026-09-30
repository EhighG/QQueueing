#!/bin/bash
# 대상 nginx 컨테이너의 설정을 고친다. qqueueing-agent 컨테이너 안에서 agent.sh가 부른다.
#   bash conf.sh init            /qqueueingAPI·/waiting location과 host.docker.internal 서버 블록을 넣는다. 이미 있으면 건너뛴다.
#   bash conf.sh register <url>  등록 URL의 location을 넣는다.
#   bash conf.sh delete <url>    등록 URL의 location을 뺀다.
# 흐름: 대상 컨테이너의 /etc/nginx를 이 컨테이너의 /etc/nginx로 복사 → 파이썬 스크립트로 새 nginx.conf를 만든다
#       → 대상 컨테이너에 넣는다 → nginx -t가 통과하면 reload하고, 실패하면 원래 nginx.conf로 되돌린다.
# URL 형식 검사는 agent.sh가 한다. 이 스크립트는 인자를 셸 코드로 실행하지 않는다.
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET="${TARGET_NGINX_CONTAINER:-demo-nginx}"
# init.py, register.py, delete.py가 /etc/nginx를 읽고 쓰며, 데모 nginx.conf의 include도 /etc/nginx 절대 경로다.
# 그래서 이 컨테이너의 /etc/nginx를 작업 디렉터리로 쓴다.
WORK_DIR=/etc/nginx
BACKUP=/tmp/qqueueing-agent/nginx.conf.orig

log() {
	printf '%s [conf.sh] %s\n' "$(date '+%F %T')" "$*"
}

usage() {
	echo "usage: conf.sh init | register <url> | delete <url>" >&2
	exit 2
}

# 대상 컨테이너의 /etc/nginx를 WORK_DIR로 복사하고 nginx.conf를 BACKUP에 남긴다.
pull_config() {
	rm -rf "$WORK_DIR"
	# WORK_DIR가 없을 때 docker cp는 원본 디렉터리를 WORK_DIR 이름으로 만든다(있으면 그 안에 nginx/를 만든다).
	if ! docker cp "$TARGET:/etc/nginx" "$WORK_DIR"; then
		log "$TARGET 컨테이너에서 /etc/nginx를 복사하지 못했다."
		return 1
	fi
	mkdir -p "$(dirname "$BACKUP")"
	cp "$WORK_DIR/nginx.conf" "$BACKUP"
}

# $1 파일을 대상 컨테이너의 /etc/nginx/nginx.conf로 넣고 검사한다. 통과하면 reload, 실패하면 되돌린다.
apply_config() {
	local new=$1
	if cmp -s "$new" "$BACKUP"; then
		log "바뀐 내용이 없어 반영하지 않는다."
		return 0
	fi
	if docker cp "$new" "$TARGET:/etc/nginx/nginx.conf" && docker exec "$TARGET" nginx -t; then
		docker exec "$TARGET" nginx -s reload || return 1
		log "$TARGET 에 반영하고 reload했다."
		return 0
	fi
	log "nginx -t가 실패해서 $TARGET 의 nginx.conf를 원래대로 되돌린다."
	docker cp "$BACKUP" "$TARGET:/etc/nginx/nginx.conf" || log "되돌리기도 실패했다. $TARGET 설정을 직접 확인해야 한다."
	return 1
}

case "${1:-}" in
	init)
		[[ $# -eq 1 ]] || usage
		pull_config || exit 1
		# 이미 초기화돼 있으면 init.py가 파일을 고치지 않고 끝나므로 apply_config가 건너뛴다.
		python3 "$SCRIPT_DIR/init.py" || exit 1
		apply_config "$WORK_DIR/nginx.conf"
		;;
	register)
		[[ $# -eq 2 ]] || usage
		pull_config || exit 1
		python3 "$SCRIPT_DIR/register.py" "$2" || exit 1
		apply_config "$WORK_DIR/complete.conf"
		;;
	delete)
		[[ $# -eq 2 ]] || usage
		pull_config || exit 1
		python3 "$SCRIPT_DIR/delete.py" "$2" || exit 1
		apply_config "$WORK_DIR/del.conf"
		;;
	*)
		usage
		;;
esac
