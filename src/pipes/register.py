import sys
import os
from module import Block, Http, Server, Location, Statement


def main(url):
    path = '/etc/nginx'
    if not os.path.exists(path):
        print('no path exist')
        exit(1)
    host, endpoint = Block.find_host_end(url)

    conf_string = Block.merge_files(path=path)
    b_nginx = Block.parse_string(conf_file=conf_string)
    print(b_nginx.export())
    for item in b_nginx:
        if isinstance(item, Http):
            b_http = item

    for item in b_http:
        if isinstance(item, Server):
            if '443' in item.ports and host in item.hosts:
                ssl_server = item

    top = -1
    for i, item in enumerate(ssl_server):
        if isinstance(item, Location):
            top = i
            break

    ssl_server.push(
        ind=top,
        val=Location(condition=endpoint, body=[
            Statement('proxy_pass http://qqueueing-main:8081/waiting/enter ;'),
            Statement(f'proxy_set_header Target-URL {url} ;')
        ])
    )
    print(b_nginx.export())


    b_nginx.save_file(path=path+'/complete.conf')



if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('please input arguement')
        exit(1)
    url = sys.argv[1]
    main(url)



