from module import Block, Http, Server, Location, If, Statement
import sys
from copy import deepcopy


def main():
    path = '/etc/nginx'

    full_context = Block.merge_files(path)
    b_nginx = Block.parse_string(full_context)

    for item in b_nginx:
        if isinstance(item, Http):
            b_http = item

    # check if already initialized, and if not terminate
    for item in b_http:
        if isinstance(item, Server):
            if 'host.docker.internal' in item.hosts:
                print('already init executed')
                exit(0)

    servers = []
    for item in b_http:
        if isinstance(item, Server):
            if '443' in item.ports:
                servers.append(item)

    # find index to insert qqu, waiting location block
    # it has to be a top
    for server in servers:
        top = -1
        for i, item in enumerate(server):
            if isinstance(item, Location):
                top = i
                break

        server.push(
            ind=top,
            val=Location(condition='/qqueueingAPI', body=[
                Statement('rewrite ^/qqueueingAPI/(.*) /$1 break;'),
                Statement('proxy_pass http://qqueueing-main:8081;')
            ])
        )
        server.push(
            ind=top,
            val=Location(condition='/waiting', body=[
                Statement('proxy_set_header address $host$request_uri;'),
                If(condition='($uri ~ ^(.*)_next/ )', body=[
                    Statement('rewrite ^(.*)$ /waiting/next break;'),
                    Statement('proxy_pass http://qqueueing-main:8081;')
                ]),
                If(condition='($uri ~ ^(.*)png )', body=[
                    Statement('rewrite ^(.*)$ /waiting/next break;'),
                    Statement('proxy_pass http://qqueueing-main:8081;')
                ]),
                If(condition='($uri ~ ^(.*)ico )', body=[
                    Statement('rewrite ^(.*)$ /favicon.ico break;'),
                    Statement('proxy_pass http://qqueueing-frontend:3000;')
                ]),
                Statement('proxy_pass http://qqueueing-main:8081$request_uri;')
            ])
        )


    real_location = None
    for server in servers:
        for item in server:
            if isinstance(item, Location) and item.condition == '/':
                real_location = deepcopy(item)

    b_http.push(
        ind=-1,
        val=Server(ports=set('80'), hosts=set('host.docker.internal'), body=[
            Statement('listen 80;'),
            Statement('listen [::]:80;'),
            Statement('server_name host.docker.internal;'),
            real_location
        ])
    )

    init_path = path + '/nginx.conf'
    b_nginx.save_file(init_path)

    pkl_path = path + 'nginx.pkl'
    b_nginx.save_pkl(pkl_path)
    # print(Block.load_pkl(init_path).export())


if __name__ == '__main__':
    main()
