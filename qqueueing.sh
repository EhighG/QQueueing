#!/bin/bash
################################################################################
##################################PATH SETTING##################################
################################################################################
URL=k10a401.p.ssafy.io
COMPOSE_PATH=src/compose.yml

################################################################################
################################################################################
################################################################################

usage() {
    printf "Usage: \n\t QQueueuing [command]"
    printf "\n\nAvailable Commands:"
    printf "\n\tstart\t\t\tStart all services"
    printf "\n\tstop\t\t\tStop all services"
    printf "\n\trestart\t\t\tRestart all stopped and running services"
    printf "\n\tlogs [-f]\t\tShow openvidu logs."
    printf "\n\tkms-logs [-f]\t\tShow kms logs"
    printf "\n\tupgrade\t\t\tUpgrade to the latest Openvidu version"
    printf "\n\tupgrade [version]\tUpgrade to the specific Openvidu version"
    printf "\n\tversion\t\t\tShow version of Openvidu Server"
    printf "\n\treport\t\t\tGenerate a report with the current status of Openvidu"
    printf "\n\thelp\t\t\tShow help for openvidu command"
    printf "\n"
}

make_env() {
	tee .env << EOF
ROOT_DIR=$(git rev-parse --show-toplevel)
EOF

echo "initial env created"
}

# need to verify docker, compose exists
# need to check their version
# need to check user set files
case $1 in
  build)
	if [[ -z $2 ]];then
	  printf "\tno extra input\n"
	  printf "\tbuild all application\n"
	  bash $PWD/src/build/main/build.sh
	fi
	echo "build images to run containers"
	echo $PWD

	# front, main, con, pro, kaf, 
	;;

  

  start)
	# if end, need to be deleted
	if [[ -z .env ]];then
		echo "first install"
		make_env
	fi

	mkfifo src/pipes/pipe
	sudo -s source src/pipes/listen.sh &
	echo $! > src/pipes/id.txt
	exit 0
    docker-compose up -f $COMPOSE_PATH -d
    if [[ "${FOLLOW_OPENVIDU_LOGS}" == "true" ]]; then
      docker-compose logs -f --tail 10 openvidu-server
    fi
    ;;

  stop)
    docker-compose down
    ;;

  restart)
    docker-compose down
    docker-compose up -d
    if [[ "${FOLLOW_OPENVIDU_LOGS}" == "true" ]]; then
      docker-compose logs -f --tail 10 openvidu-server
    fi
    ;;

  logs)
    case "${2-}" in
      --follow|-f)
        docker-compose logs -f --tail 10 openvidu-server
        ;;
      *)
        docker-compose logs openvidu-server
        ;;
    esac
    ;;

  kms-logs)
    kurento_logs "$2"
    ;;


  version)
    version_ov
    ;;

  *)
    usage
    ;;
esac
