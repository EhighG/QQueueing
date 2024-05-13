import re
import sys
import os
import glob
import copy
from module import Block

def merge_files(path):
    conf_dir = os.path.dirname(path)
    if len(conf_dir) != 0: os.chdir(conf_dir)

    include_directives = '^\s*include\s*(.*);'
    tmptext=''
    with open(path+'/nginx.conf', 'r') as f:
        for line in f.readlines():
            included = re.findall(include_directives, line)
            if len(included) > 0:
                include_files = glob.glob(included[0])
                for file in include_files:
                    if os.path.exists(file):
                        with open(file, 'r') as ff:
                            include_con = ff.read()
                            tmptext += include_con
            else:
                tmptext += line

    # remove comments
    del_cmt_str = ''
    quote_flag = False
    comment_flag = False
    for i, text in enumerate(tmptext):
        if text in ['\'', '\"'] and not comment_flag:
            quote_flag = not quote_flag
        elif text == '#' and not quote_flag:
            comment_flag = True
        elif text == '\n' and comment_flag:
            comment_flag = False

        if not comment_flag:
            del_cmt_str += text
    print(del_cmt_str)
    return del_cmt_str

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
    save_path = path+'/complete.conf'
    if os.path.exists(save_path):
        os.remove(save_path)
    os.chmod(path, 664)
    with open(save_path, 'w') as f:
        f.write(complete_file)


def main(url):
    path = '/etc/nginx'
    if not os.path.exists(path):
        print('no path exist')
        exit(1)
    full_text = merge_files(path)
    #print(full_text)

    host, endpoint = find_host_end(url)
    print('parsing url  ::',host, endpoint)


    nginx_block = Block.file_import(full_text)
    http_block = nginx_block.get(nginx_block.find_by_type('http')[0])
    
    servers = http_block.find_by_type('server')

    # add two location
    ssl_server = None
    for i in servers:
        #print(http_block.get(i).export())
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
            # if user just takes all vh by default like _? 
            # this code cannot handle that
    real_location = None
    locations = ssl_server.find_by_type(type='location')
    for i in locations:
        if ssl_server.get(i).cond == '/':
            real_location = copy.deepcopy(ssl_server.get(i))




    location_index = -1
    try:
        location_index = ssl_server.find_by_type(type='location')[0] 
    except:
        pass

    intercepting_location = Block(type='location')
    intercepting_location.cond = endpoint
    tmp_block = Block(type='statement', bodies=['proxy_pass http://qqueueing-main:8081/waiting/enter ;'])
    intercepting_location.push(ind=-1, val=tmp_block)
    tmp_block = Block(type='statement', bodies=[f'proxy_set_header Target-URL {url} ;'])
    intercepting_location.push(ind=-1, val=tmp_block)

    ssl_server.push(ind=location_index, val=intercepting_location)


    # this val should be changeable, because this can be already in use
    waiting_location = Block(type='location')
    waiting_location.cond = '/waiting'

    tmp_block = Block(type='statement', bodies=['proxy_set_header address $host$request_uri ;'])
    waiting_location.push(ind=-1, val=tmp_block)

    if_block = Block(type='if')
    if_block.cond = '($uri ~ ^(.*)_next/)'
    tmp_block = Block(type='statement', bodies=['rewrite ^(.*)$ /waiting/next break ;'])
    if_block.push(ind=-1, val=tmp_block)
    tmp_block = Block(type='statement', bodies=['proxy_pass http://qqueueing-main:8081 ;'])
    if_block.push(ind=-1, val=tmp_block)
    waiting_location.push(ind=-1, val=if_block)

    if_block = Block(type='if')
    if_block.cond = '($uri ~ ^(.*)png )'
    tmp_block = Block(type='statement', bodies=['rewrite ^(.*)$ /waiting/next break ;'])
    if_block.push(ind=-1, val=tmp_block)
    tmp_block = Block(type='statement', bodies=['proxy_pass http://qqueueing-main:8081 ;'])
    if_block.push(ind=-1, val=tmp_block)
    waiting_location.push(ind=-1, val=if_block)

    tmp_block = Block(type='statement', bodies=['proxy_pass http://qqueueing-main:8081$request_uri ;'])
    waiting_location.push(ind=-1, val=tmp_block)

    ssl_server.push(ind=location_index, val=waiting_location)

    


    # add one server
    if http_block.get(servers[-1]).host == 'host.docker.internal':
        internal_server = http_block.get(servers[-1])
    else:
        internal_server = Block(type='server')
        internal_server.host = 'host.docker.internal'
        internal_server.port = '80'
        http_block.push(ind=-1, val=internal_server)
        statement_block = Block(type='statement', bodies=['listen 80 ;'])
        internal_server.push(ind=-1, val=statement_block)
        statement_block = Block(type='statement', bodies=['listen [::]:80 ;'])
        internal_server.push(ind=-1, val=statement_block)
        statement_block = Block(type='statement', bodies=['server_name host.docker.internal ;'])
        internal_server.push(ind=-1, val=statement_block)

    # need to get user's front url
#    direct_location = Block(type='location')
#    direct_location.cond = endpoint
#    statement_block = Block(type='statement', bodies=[f'proxy_pass http://frontend:3000 ;'])
#    direct_location_server.push(ind=-1, val=statement_block)
#    internal_server.push(ind=-1, val=direct_location)

    internal_server.push(ind=-1, val=real_location)


    print(nginx_block.export(indent=-1))

#    complete_file = insert_location(full_text, url)
#
    save_conf_file(path, nginx_block.export(indent=-1))
#    print('save path is', path)
#    with open(path+'/complete.conf', 'r') as f:
#        for line in f.readlines():
#            print(line)

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('please input arguement')
        exit(1)
    url = sys.argv[1]
    main(url)



