# URL 등록 에이전트(qqueueing-agent). docker CLI로 대상 nginx 컨테이너의 설정을 고치고 reload한다.
FROM docker:29.3.1-cli
RUN apk add --no-cache bash python3 tzdata
ENV TZ=Asia/Seoul
WORKDIR /opt/qqueueing-agent
COPY pipes/agent.sh pipes/conf.sh pipes/module.py pipes/init.py pipes/register.py pipes/delete.py ./
ENTRYPOINT ["bash", "/opt/qqueueing-agent/agent.sh"]
