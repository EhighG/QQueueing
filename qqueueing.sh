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
  printf "\033[34m" 

  echo ' $$$$$$\   $$$$$$\                                          $$\                     ';
  echo '$$  __$$\ $$  __$$\                                         \__|                    ';
  echo '$$ /  $$ |$$ /  $$ |$$\   $$\  $$$$$$\  $$\   $$\  $$$$$$\  $$\ $$$$$$$\   $$$$$$\  ';
  echo '$$ |  $$ |$$ |  $$ |$$ |  $$ |$$  __$$\ $$ |  $$ |$$  __$$\ $$ |$$  __$$\ $$  __$$\ ';
  echo '$$ |  $$ |$$ |  $$ |$$ |  $$ |$$$$$$$$ |$$ |  $$ |$$$$$$$$ |$$ |$$ |  $$ |$$ /  $$ |';
  echo '$$ $$\$$ |$$ $$\$$ |$$ |  $$ |$$   ____|$$ |  $$ |$$   ____|$$ |$$ |  $$ |$$ |  $$ |';
  echo '\$$$$$$ / \$$$$$$ / \$$$$$$  |\$$$$$$$\ \$$$$$$  |\$$$$$$$\ $$ |$$ |  $$ |\$$$$$$$ |';
  echo ' \___$$$\  \___$$$\  \______/  \_______| \______/  \_______|\__|\__|  \__| \____$$ |';
  echo '     \___|     \___|                                                      $$\   $$ |';
  echo '                                                                          \$$$$$$  |';
  echo '                                                                           \______/ ';
  printf "\033[0m\n"
  printf "\t\033[31mNOTICE: If this is the first time using QQueueing, please input command install.\033[0m\n"
  printf "Usage: \n\tQQueueuing [command]"
  printf "\n\nAvailable Commands:"
  printf "\n\t\033[0;32m(i)nstall\t\tIntialize before start service\033[0m"
  printf "\n\t\033[0;32mstart\t\t\tStart all services\033[0m"
  printf "\n\t\033[0;32mstop\t\t\tStop all services\033[0m"
  printf "\n\t\033[0;32m(r)estart\t\tRestart all stopped and running services\033[0m"
  printf "\n\thelp\t\t\tShow help for QQueueing command\n"
  printf "\n"
}

make_env() {
	printf "\033[42m CREATE ENV FILE\033[0m\n" 
	echo "Please enter your url, e.g. \033[0;34mhttps://www.ssafy.com\033[0m"
	read url

	tee .env << EOF
################################################################################
####################################SETTING#####################################
################################################################################
# This file is for your customization.
# Please write only empty input parts.

# Enter the domain name of your web site. 
# e.g. URL=https:/www.ssafy.com
URL=$url

# This port is admin page fo waiting room.
# default is 3001, but if you use this port already, please change it.
PORT=3001

# This var should not be changed unless you change your project directory.
ROOT_DIR=$(git rev-parse --show-toplevel)


EOF
	printf "\033[42m ENV FILE CREATED\033[0m\n" 
}

stop_service() {
  docker compose --env-file .env -f $COMPOSE_PATH down
  sudo kill $(cat src/pipes/id.txt)
  sudo rm src/pipes/id.txt src/pipes/pipe
}

start_service() {
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

  sudo docker exec $CONTAINER_NAME nginx -t
  sudo docker exec $CONTAINER_NAME nginx -s reload
}

# need to verify docker, compose exists
docker -v > /dev/null && docker compose version > /dev/null ||\
	echo "Make sure you installed docker and compose"

# If user not in docker group, add in group.
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
	if [[ ! -e .env ]];then
		make_env
	fi
	# TODO: check if .env file is valid
	# TODO: if not, wait for user's input
	# from .env, read url info.
	source .env

	# check qqueueing-network exists
	if sudo docker network ls | grep -w "qqueueing-network" > /dev/null 2>&1; then
		echo "qqueueing-network already exists."
	else
		sudo docker network create qqueueing-network
	fi
	sudo docker network connect qqueueing-network $CONTAINER_NAME 2> /dev/null

	# initalizing user's nginx
	if [[ -d $NGINX_PATH ]];then
		sudo rm -rf $NGINX_PATH
	fi
	sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH
	if [[ -z $NGINX_PATH/nginx.conf.save ]]; then
		echo "archiving original file"
		sudo docker exec $CONTAINER_NAME cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.save
	fi

	# inital config
	sudo python3 src/scripts/insert_domain.py $URL
	# In current, all server blocks get initial setting
	sudo python3 src/pipes/init.py
	sudo docker cp $NGINX_PATH/nginx.conf $CONTAINER_NAME:/etc/nginx/nginx.conf
	sudo rm -rf $NGINX_PATH
  ;;

  start)
	# TODO: check if user already started service
	start_service
    ;;

  stop)
	# TODO: check if user already stopped service
	stop_service
    ;;

  ps)
    docker compose --env-file .env -f $COMPOSE_PATH ps -a
    ;;

  build)
	if [[ -z $2 ]];then
	  printf "\tno extra input\n"
	  printf "\tbuild all application\n"
	fi
    docker compose -f $COMPOSE_PATH build
	echo "build images to run containers"
	;;

  restart|r)
	stop_service
	start_service
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
