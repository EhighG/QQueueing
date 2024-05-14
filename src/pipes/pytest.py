from module import Block 
import sys

print('=============================================')


def specialize(block: Block) -> Block:
    '''
    act in http block
    distinguish server, upstream block
    params: http block
    return: http block
    '''
    if block.type != 'http': 
        raise Exception('specialize only gets http block')
    for i in block.bodies:
        variables = {}
        if i.type == 'server':
            for j in i.bodies:
                if j.type == 'statement':
                    if 'listen' in j.bodies[0]:
                        number = j.bodies[0].split()[1].strip(';')
                        if number.isdigit():
                            i.ports.append(number)

                    elif 'server_name' in j.bodies[0]:
                        lst = j.bodies[0].split()
                        for k in lst[1:]:
                            host = k.strip(';')
                            if not host: break
                            if host in variables:
                                i.hosts.append(variables[host])
                            else:
                                i.hosts.append(host)

                    elif 'set' in j.bodies[0]:
                        lst = j.bodies[0].split()
                        variables[lst[1]] = lst[2]
        elif i.type == 'upstream':
            pass
    return block

def insert_default_block(block: Block, host) -> Block:
    if block.type != 'http':
        raise Exception("plz input http")

    # insert two location block in ssl server
    ## find ssl server
    ssl_server = None
    for server in block.bodies:
        if server.type == 'server':
            if '443' in server.ports and host in server.hosts:
                ssl_server = server
    ## insert in the top
    locations = ssl_server.find_by_type(type='location')
    loc_location = -1 if not locations else locations[0]

    qqu_location = Block(
            type='location',
            cond='/qqueueingAPI',
            bodies=[
                Block(
                    type='statement',
                    bodies=[
                        'rewrite ^/qqueueingAPI/(.*) /$1 break;'
                        ]
                    ),
                Block(
                    type='statement',
                    bodies=[
                        'proxy_pass http:// qqueueing-main:8081;'
                        ]
                    ),
                ]
            )
    ssl_server.push(ind=loc_location, val=qqu_location)

    waiting_location = Block(
        type='location',
        cond='/waiting',
        bodies=[
            Block(
                type='statement',
                bodies=['proxy_set_header address $host$request_uri;']
            ),
            Block(
                type='if',
                cond='($uri ~ ^(.*)_next/ )',
                bodies=[
                    Block(
                        type='statement',
                        bodies=['rewrite ^(.*)$ /waiting/next break;']
                    ),
                    Block(
                        type='statement',
                        bodies=['proxy_pass http://qqueueing-main:8081;']
                    )
                ]
            ),
            Block(
                type='if',
                cond='($uri ~ ^(.*)png )',
                bodies=[
                    Block(
                        type='statement',
                        bodies=['rewrite ^(.*)$ /waiting/next break;']
                    ),
                    Block(
                        type='statement',
                        bodies=['proxy_pass http://qqueueing-main:8081;']
                    )
                ]
            ),
            Block(
                type='if',
                cond='($uri ~ ^(.*)ico )',
                bodies=[
                    Block(
                        type='statement',
                        bodies=['rewrite ^(.*)$ /favicon.ico break;']
                    ),
                    Block(
                        type='statement',
                        bodies=['proxy_pass http://qqueueing-frontend:3000;']
                    )
                ]
            ),
            Block(
                type='statement',
                bodies=['proxy_pass http://qqueueing-main:8081$request_uri;']
            ),

        ]
    )
    ssl_server.push(ind=loc_location, val=waiting_location)




    # insert one server block named host.docker.internal
    ## insert in last 
    last_server = Block(
        type='server',
        ports=['80'],
        hosts=['host.docker.internal'],
        bodies=[
            Block(
                type='statement',
                bodies=['listen 80;']
            ),
            Block(
                type='statement',
                bodies=['listen [::]80;']
            ),
            Block(
                type='statement',
                bodies=['server_name host.docker.internal;']
            ),
        ]
    )

    return true




host, endpoint = Block.find_host_end(sys.argv[1])

path = '/etc/nginx/nginx.conf'
#with open(path, 'r') as f:
#    print(f.read())

path = '/etc/nginx'
full_context = Block.merge_files(path)


nginx_block = Block.file_import(full_context)
http_block = None
for i in nginx_block.bodies:
    if i.type == 'http':
        http_block = i
        break

http_block = specialize(http_block)

insert_default_block(http_block, host)



