#!/bin/bash
if [[ -z $2 ]];then
	echo "needed exact 2 arguements"
	exit 1
fi
#0 set variable
CONTAINER_NAME="test-nginx"
NGINX_PATH="/etc/nginx"
URL_PATH=$2
COMPLETE_FILE="/complete.conf"


case $1 in
	register)
		echo "set nginx!!"

		# this is only for test. must not forget
		echo "initialize nginx for test"
		GIT_ROOT=$(git rev-parse --show-toplevel)
		$GIT_ROOT/test/test.sh nginx -d
		docker network connect qqueueing_qqueueing-network $CONTAINER_NAME
		# this is for test
		sudo rm -rf $NGINX_PATH



		#1 copy nginx files from container
		# todo: if user have nginx in host, need to handle that
		if test -d $NGINX_PATH
		then echo "path already exists"
			exit 0
		fi
		sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH
		sudo touch $NGINX_PATH$COMPLETE_FILE

		# if in first setting, nginx container must include our network

		#2 execute python script
		sudo python3 nginx.py $URL_PATH
		sudo chmod 664 $NGINX_PATH$COMPLETE_FILE
		sudo cat $NGINX_PATH$COMPLETE_FILE | grep $URL_PATH

		#3 copy completed file to contianer 
		sudo docker exec $CONTAINER_NAME mv /etc/nginx/nginx.conf /etc/nginx/nginx.conf.save
		sudo docker cp $NGINX_PATH$COMPLETE_FILE $CONTAINER_NAME:/etc/nginx/nginx.conf

		sudo rm -rf $NGINX_PATH

		sudo docker exec $CONTAINER_NAME nginx -t 
		#exit 1

		#4 restart nginx
		sudo docker exec $CONTAINER_NAME nginx -s reload
		exit 0
		;;
	delete)
		echo "delete"
		sudo docker exec $CONTAINER_NAME rm /etc/nginx/nginx.conf
		sudo docker exec $CONTAINER_NAME mv /etc/nginx/nginx.conf.save /etc/nginx/nginx.conf
		sudo docker exec $CONTAINER_NAME nginx -s reload
		;;
	modify)
		echo "modify"
		;;
	*)
		echo "Not valid arguement"
		exit 1
		;;
esac




