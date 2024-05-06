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
    printf "\n\tversion\t\t\tShow version of Openvidu Server"
    printf "\n\thelp\t\t\tShow help for openvidu command"
    printf "\n"
}

make_env() {
	tee .env << EOF
################################################################################
####################################SETTING#####################################
################################################################################
# This file is for your customization.

# This var should not be changed unless you change your project directory.
ROOT_DIR=$(git rev-parse --show-toplevel)

EOF

echo "initial env created"
}

# need to verify docker, compose exists
docker -v && docker compose version ||\
	echo "Make sure you installed docker and compose"
if [[ -z $(groups | grep docker) ]]; then
	echo "add user in docker group to avoid additional sudo privileges"
	sudo usermod -aG docker ${USER} 
	sudo service docker restart
	echo "Please enter into new terminal. "
	exit 0
fi
# need to check their version
# need to check user set files
case $1 in
  build)
	if [[ -z $2 ]];then
	  printf "\tno extra input\n"
	  printf "\tbuild all application\n"
	fi
    docker compose -f $COMPOSE_PATH build
	echo "build images to run containers"
	;;

  

  start)
	# if end, need to be deleted
	if [[ ! -e .env ]];then
		make_env
	fi

	if [[ -e src/pipes/id.txt ]];then
		echo "reconnect pipe"
		sudo kill $(cat src/pipes/id.txt)
		sudo rm src/pipes/id.txt src/pipes/pipe
	fi

	mkfifo src/pipes/pipe
	sudo -s source src/pipes/listen.sh &
	echo $! > src/pipes/id.txt
    docker compose -f $COMPOSE_PATH build
    docker compose -f $COMPOSE_PATH up -d
    ;;

  stop)
    docker compose -f $COMPOSE_PATH down
	sudo kill $(cat src/pipes/id.txt)
	sudo rm src/pipes/id.txt src/pipes/pipe
    ;;

  restart)
    docker compose -f $COMPOSE_PATH down
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


  version)
    version_ov
    ;;

  *)
    usage
    ;;
esac
