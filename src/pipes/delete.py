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
    real_location = None

    locations = ssl_server.find_by_type(type='location')
    del_loc_ind = []
    for i in locations:
        if ssl_server.get(i).cond == endpoint:
            del_loc_ind.append(i)
        elif ssl_server.get(i).cond == '/waiting':
            del_loc_ind.append(i)
    del_loc_ind.sort(reverse=True)
    for i in del_loc_ind:
        ssl_server.pop(ind=i)
    http_block.pop(ind=servers[-1])

    print(nginx_block.export())
    path = '/etc/nginx'
    save_conf_file(path, nginx_block.export())

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('please input arguement')
        exit(1)
    url = sys.argv[1]
    main(url)
