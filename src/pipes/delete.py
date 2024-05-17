import sys
import os
from module import Block, Http, Server, Location, Statement


def main(url):
    path = '/etc/nginx'
    if not os.path.exists(path):
        print('no path exist')
        exit(1)
    # assume that register py execute
    host, endpoint = Block.find_host_end(url)
    conf_string = Block.merge_files(path=path)
    b_nginx = Block.parse_string(conf_file=conf_string)

    for item in b_nginx:
        if isinstance(item, Http):
            b_http = item
    
    for item in b_http:
        if isinstance(item, Server):
            if '443' in item.ports and host in item.hosts:
                ssl_server = item
    for i, item in enumerate(ssl_server):
        if isinstance(item, Location) and item.condition == endpoint:
            ssl_server.pop(ind=i)
            break
    b_nginx.save_file(path=path+'/del.conf')
    

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('please input arguement')
        exit(1)
    url = sys.argv[1]
    main(url)
