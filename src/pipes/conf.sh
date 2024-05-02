#!/bin/bash
echo "set nginx!!"

if [[ -z $1 ]]; then
	echo "no input"
	exit 1
fi

#./test.sh nginx -d

#0 set variable
CONTAINER_NAME="test-nginx"
NGINX_PATH="/etc/nginx"
URL_PATH=$1
COMPLETE_FILE="/complete.conf"

sudo rm -rf $NGINX_PATH
#1 copy nginx files from container
if test -d $NGINX_PATH
then echo "path already exists"
	exit 0
fi
sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH
sudo touch $NGINX_PATH$COMPLETE_FILE
#ls -al $NGINX_PATH

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
