#!/bin/bash

sudo -s source delete.sh
sudo -s source listen.sh &
echo $! > id.txt
