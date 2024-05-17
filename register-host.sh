#!/bin/bash

# not yest tested, but this file might be useful when nginx is in host

# set variables
API=qqueueing-api
API_URL=$(docker network inspect qqueueing_qqueueing-network | jq -r '.[] | .Containers[] | {(.Name): .IPv4Address}| select( ."qqueueing-qqueueing-main-1") | .["qqueueing-qqueueing-main-1"]'| cut -d'/' -f1)

# make save file
sudo cp /etc/hosts /etc/hosts.old

# add hostfile
if [[ -z $(cat /etc/hosts | grep $API ) ]];then
 echo "$API_URL $API" | sudo tee -a /etc/hosts /dev/null
 echo "api server regitster complete"
fi


