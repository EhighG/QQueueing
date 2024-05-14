import re
import sys
import os
import glob
import copy
from module import Block

def find_host_end(url):
    url = url.strip()
    start_index = 0
    host = endpoint = 0
    if url.startswith("http"):
        start_index = 8 if url[4] == 's' else 7

    for i, val in enumerate(url):
        if i < start_index: continue
        if val == '/':
            host = url[start_index:i]
            endpoint = url[i:]
            break
    return [host, endpoint]

def save_conf_file(path, complete_file):
    save_path = path+'/del.conf'
    if os.path.exists(save_path):
        os.remove(save_path)
    with open(save_path, 'w') as f:
        f.write(complete_file)

def main(url):
    path = '/etc/nginx'
    if not os.path.exists(path):
        print('no path exist')
        exit(1)
    # assume that register py execute
    full_text = ''
    with open('/etc/nginx/nginx.conf', 'r')as f:
    #with open('/etc/nginx/complete.conf', 'r')as f:
        full_text = f.read()

    host, endpoint = find_host_end(url)
    print('parsing url  ::',host, endpoint)

    nginx_block = Block.file_import(full_text)
    http_block = nginx_block.get(nginx_block.find_by_type('http')[0])
    
    servers = http_block.find_by_type('server')

    ssl_server = None
    for i in servers:
        port = False
        domain = False
        for j in http_block.get(i).bodies:
            if j.type == 'statement':
                if '443' in j.bodies[0]:
                    port = True
                if host in j.bodies[0]:
                    domain = True
            if port and domain: 
                ssl_server = http_block.get(i)
                break

    locations = ssl_server.find_by_type(type='location')
    for i in locations:
        if ssl_server.get(i).cond == endpoint:
            ssl_server.pop(ind=i)
            break
#    print('is it over?')
#    print(ssl_server.export())
    
#    last_server = http_block.get(-1)
#    delete_default = last_server.find(type='location', cond=endpoint)
#    for i in last_server.find_by_type(type='location'):
#        if last_server.get(i).cond == endpoint
#    if delete_default > 0:
#        last_server.pop(ind=delete_default)

    #print(nginx_block.export())
    path = '/etc/nginx'
    save_conf_file(path, nginx_block.export())

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('please input arguement')
        exit(1)
    url = sys.argv[1]
    main(url)
