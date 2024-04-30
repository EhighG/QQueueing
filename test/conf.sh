#!/bin/bash

if [[ -z $1 ]]; then
	echo "no input"
	exit 1
fi

#0 set variable
CONTAINER_NAME="test-nginx"
NGINX_PATH="/etc/nginx"

#1 copy nginx files from container
sudo docker cp $CONTAINER_NAME:/etc/nginx $NGINX_PATH

#2 execute python script
python3 nginx.py $1
cat /etc/nginx/complete.conf

exit 0
#3 copy completed file to contianer
docker exec mv /etc/nginx/nginx.conf /etc/nginx/nginx.conf.save
docker cp /etc/nginx/complete.conf $CONTAINER_NAME:/etc/nginx/nginx.conf

#4 restart nginx
docker exec nginx -s reload
