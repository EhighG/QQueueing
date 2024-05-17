#!/bin/bash
################################################################################
##################################PATH SETTING##################################
################################################################################
COMPOSE_PATH=src/compose.yml
NGINX_PATH="/etc/nginx"
INIT_FILE="/init.conf"
COMPLETE_FILE="/complete.conf"
SAVE_FILE="/nginx.conf.save"
CONTAINER_NAME=$(docker ps -a | grep ">80/tcp"| awk '{print $NF}')

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
# Please write only empty input parts.

# Enter the domain name of your web site. 
# e.g. URL=https:/www.ssafy.com
URL=

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
	echo "Please restart your terminal. "
	exit 0
fi


# need to check their version
# need to check user set files
case $1 in
  install|i)
	# TODO: check if .env exists, if not make env

	
	# TODO: check if .env file is valid
	# TODO: if not, wait for user's input
	# TODO: if good, execute default setting
	# TODO: default -> insert hosts to .env, application.yml
	# TODO: default -> execute init.py

  
  

  ;;





  build)
	if [[ -z $2 ]];then
	  printf "\tno extra input\n"
	  printf "\tbuild all application\n"
	fi
    docker compose -f $COMPOSE_PATH build
	echo "build images to run containers"
	;;

  

  start)
	sudo docker network create qqueueing-network
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
    docker compose --env-file .env -f $COMPOSE_PATH build
    docker compose --env-file .env -f $COMPOSE_PATH up -d

	# initalizing user's nginx
	# it is only for nginx in docker
	sudo docker network connect qqueueing-network $CONTAINER_NAME 2> /dev/null
	if [[ -d $NGINX_PATH ]];then
		sudo rm -rf $NGINX_PATH
	fi
	sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH
	if [[ -z $NGINX_PATH/nginx.conf.save ]]; then
		echo "archiving original file"
		sudo docker exec $CONTAINER_NAME cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.save
	fi
	sudo python3 ./src/pipes/init.py
	sudo docker cp $NGINX_PATH/nginx.conf $CONTAINER_NAME:/etc/nginx/nginx.conf

	sudo docker exec $CONTAINER_NAME nginx -t
	sudo docker exec $CONTAINER_NAME nginx -s reload
    ;;

  stop)
    docker compose --env-file .env -f $COMPOSE_PATH down
	sudo kill $(cat src/pipes/id.txt)
	sudo rm src/pipes/id.txt src/pipes/pipe
    ;;

  ps)
    docker compose --env-file .env -f $COMPOSE_PATH ps -a
    ;;


  restart)
    docker compose --env-file .env -f $COMPOSE_PATH down
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
