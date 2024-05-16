#!/bin/bash

# test

if [[ -z $2 ]];then
	echo "needed exact 2 arguements"
	exit 1
fi
#0 set variable
NGINX_PID=$(ps -ef | grep "nginx: master"| head -1 | awk '{print $2 }')
DIFF=$(diff <(sudo ls -Al /proc/1/ns | awk '{ print $11 }')  <(sudo ls -Al /proc/$NGINX_PID/ns | awk '{ print $11 }'))
if [[ -z $DIFF ]];then
	echo "Your nginx is in your host condition"
else
	echo "Your nginx is in container"
	IS_NGNIX_CTNR=1
fi
CONTAINER_NAME=$(docker ps -a | grep ">80/tcp"| awk '{print $NF}')
sudo docker network connect qqueueing_qqueueing-network $CONTAINER_NAME 2> /dev/null
NGINX_PATH="/etc/nginx"
URL_PATH=$2
INIT_FILE="/init.conf"
COMPLETE_FILE="/complete.conf"
DETETE_FILE="/del.conf"
SAVE_FILE="/nginx.conf.save"
ROOT_DIR=$(git rev-parse --show-toplevel)



case $1 in
	register)
		echo "set nginx!!"

		if [[ -n $IS_NGNIX_CTNR ]];then
			#1 copy nginx files from container
			# todo: if user have nginx in host, need to handle that
			if [[ -d $NGINX_PATH ]];then
				sudo rm -rf $NGINX_PATH
			fi
			sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH
			sudo touch $NGINX_PATH$COMPLETE_FILE


			#2 execute python script
			echo "REGISTERING $URL_PATH"
			sudo python3 $ROOT_DIR/src/pipes/register.py $URL_PATH
			sudo chmod 664 $NGINX_PATH$COMPLETE_FILE

			#3 copy completed file to contianer 
			sudo docker cp $NGINX_PATH$COMPLETE_FILE $CONTAINER_NAME:/etc/nginx/nginx.conf

			sudo rm -rf $NGINX_PATH

			sudo docker exec $CONTAINER_NAME nginx -t 

			#4 restart nginx
			sudo docker exec $CONTAINER_NAME nginx -s reload
		else
			sudo rm -rf $NGINX_PATH$COMPLETE_FILE 2> /dev/null
			sudo touch $NGINX_PATH$COMPLETE_FILE
			sudo python3 $ROOT_DIR/register.py $URL_PATH
			sudo chmod 664 $NGINX_PATH$COMPLETE_FILE
			sudo cat $NGINX_PATH$COMPLETE_FILE | grep $URL_PATH

			sudo mv $NGINX_PATH/nginx.conf $NGINX_PATH/nginx.conf.save
			sudo mv $NGINX_PATH$COMPLETE_FILE $NGINX_PATH/nginx.conf

			sudo nginx -s reload
		fi
		;;
	delete)
		echo "delete"
		if [[ -n $IS_NGNIX_CTNR ]];then
			#1 copy nginx files from container
			# todo: if user have nginx in host, need to handle that
			if [[ -d $NGINX_PATH ]];then
				sudo rm -rf $NGINX_PATH
			fi
			sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH
			sudo touch $NGINX_PATH$DETETE_FILE


			#2 execute python script
			echo "DELETING $URL_PATH"
			sudo python3 $ROOT_DIR/src/pipes/delete.py $URL_PATH
			sudo chmod 664 $NGINX_PATH$DETETE_FILE

			#3 copy completed file to contianer 
			sudo docker cp $NGINX_PATH$DETETE_FILE $CONTAINER_NAME:/etc/nginx/nginx.conf

			sudo rm -rf $NGINX_PATH

			sudo docker exec $CONTAINER_NAME nginx -t 

			#4 restart nginx
			sudo docker exec $CONTAINER_NAME nginx -s reload
		else
			sudo python3 $ROOT_DIR/src/pipes//delete.py $URL_PATH
			sudo chmod 664 $NGINX_PATH$DETETE_FILE
			sudo cat $NGINX_PATH$DETETE_FILE | grep $URL_PATH

			sudo mv $NGINX_PATH/nginx.conf $NGINX_PATH/nginx.conf.save
			sudo mv $NGINX_PATH$DETETE_FILE $NGINX_PATH/nginx.conf

			sudo nginx -s reload
		fi
		;;
	modify)
		echo "modify"
		;;
	dr-test)
		echo "test for delete"
		if [[ -d $NGINX_PATH ]];then
			sudo rm -rf $NGINX_PATH
		fi
		sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH


		#2 execute python script
		echo "DELETING $URL_PATH"
		sudo python3 delete.py $URL_PATH
		sudo chmod 664 $NGINX_PATH$DETETE_FILE
		sudo cat $NGINX_PATH$DETETE_FILE 

		#3 copy completed file to contianer 
		sudo docker cp $NGINX_PATH$DETETE_FILE $CONTAINER_NAME:/etc/nginx/nginx.conf

		sudo rm -rf $NGINX_PATH

		sudo docker exec $CONTAINER_NAME nginx -t 


		echo "test for register"
		if [[ -d $NGINX_PATH ]];then
			sudo rm -rf $NGINX_PATH
		fi
		sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH


		#2 execute python script
		echo "REGITSTERING $URL_PATH"
		sudo python3 /src/pipes/register.py $URL_PATH
		sudo chmod 664 $NGINX_PATH$COMPLETE_FILE
		sudo cat $NGINX_PATH$COMPLETE_FILE 

		#3 copy completed file to contianer 
		sudo docker cp $NGINX_PATH$COMPLETE_FILE $CONTAINER_NAME:/etc/nginx/nginx.conf

		sudo rm -rf $NGINX_PATH

		sudo docker exec $CONTAINER_NAME nginx -t 
		;;

	rd-test)
		if [[ -n $IS_NGNIX_CTNR ]];then
			#1 copy nginx files from container
			# todo: if user have nginx in host, need to handle that
			if [[ -d $NGINX_PATH ]];then
				sudo rm -rf $NGINX_PATH
			fi
			sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH
			sudo touch $NGINX_PATH$COMPLETE_FILE


			#2 execute python script
			echo "REGITSTERING $URL_PATH"
			sudo python3 register.py $URL_PATH
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
		else
			sudo rm -rf $NGINX_PATH$COMPLETE_FILE 2> /dev/null
			sudo touch $NGINX_PATH$COMPLETE_FILE
			sudo python3 register.py $URL_PATH
			sudo chmod 664 $NGINX_PATH$COMPLETE_FILE
			sudo cat $NGINX_PATH$COMPLETE_FILE | grep $URL_PATH

			sudo mv $NGINX_PATH/nginx.conf $NGINX_PATH/nginx.conf.save
			sudo mv $NGINX_PATH$COMPLETE_FILE $NGINX_PATH/nginx.conf

			sudo nginx -s reload
		fi
		echo "test for register"
		if [[ -d $NGINX_PATH ]];then
			sudo rm -rf $NGINX_PATH
		fi
		sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH


		#2 execute python script
		echo "REGITSTERING $URL_PATH"
		sudo python3 register.py $URL_PATH
		sudo chmod 664 $NGINX_PATH$COMPLETE_FILE
		sudo cat $NGINX_PATH$COMPLETE_FILE 

		#3 copy completed file to contianer 
		sudo docker cp $NGINX_PATH$COMPLETE_FILE $CONTAINER_NAME:/etc/nginx/nginx.conf

		sudo rm -rf $NGINX_PATH

		sudo docker exec $CONTAINER_NAME nginx -t 

		echo "test for delete"
		if [[ -d $NGINX_PATH ]];then
			sudo rm -rf $NGINX_PATH
		fi
		sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH


		#2 execute python script
		echo "DELETING $URL_PATH"
		sudo python3 delete.py $URL_PATH
		sudo chmod 664 $NGINX_PATH$DETETE_FILE
		sudo cat $NGINX_PATH$DETETE_FILE 

		#3 copy completed file to contianer 
		sudo docker cp $NGINX_PATH$DETETE_FILE $CONTAINER_NAME:/etc/nginx/nginx.conf

		sudo rm -rf $NGINX_PATH

		sudo docker exec $CONTAINER_NAME nginx -t 
		;;
	*)
		echo "Not valid arguement"
		exit 1
		;;
esac




