import re
import sys
import os
import glob

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

    fulltext = ''

    # remove comments
    flag = True
    fulltext=''
    for i,text in enumerate(tmptext):
        if text == '#':
            flag = False
        elif text == '\n':
            flag = True
        if flag:
            fulltext += text
    return fulltext

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


def insert_location(fulltext, url):
    port = 443
    host, endpoint = find_host_end(url)
    host_len = len(host)

    count = 0
    is_server = False
    is_target_port = False
    is_target_host = False

    check = -1

    for i, char in enumerate(fulltext):
        # this has risks to cause error
        if fulltext[i:i+6] == 'server':
            if fulltext[i-1].isspace() and fulltext[i+6].isspace() :
                is_server = True
        if is_server:
            if char == '{':
                count += 1
            elif char == '}':
                count -= 1
                if not count and is_target_port and is_target_host:
                    check = i
                    is_server = False
                    is_target_port = False
                    is_target_host = False
            elif fulltext[i:i+3] == str(port):
                is_target_port = True
            elif fulltext[i:i+host_len] == host:
                is_target_host = True

    if check == -1:
        print('check your url is valid')
        exit(1)
    # send to api server
    #waiting_view_url = "http://" + host + f':3001/waiting/1?Target-URL={url}'
    host= 'qqueueing-main:8081'
    waiting_view_url = "http://" + host + f'waiting/enter'

    insert_text =f''

    insert_text+=f'location  {endpoint} {{ '

    #insert_text+=f'return 308 {waiting_view_url} ; '
    #insert_text+=f'proxy_pass  {waiting_view_url} ; '
    insert_text+=f'proxy_pass  {waiting_view_url}$request_uri ; '
    insert_text+=f'proxy_set_header Target-URL {url};'
#    insert_text+=f'proxy_set_header    Host                $host:$server_port;'
#    #insert_text+=f'proxy_set_header    Host                $host:$server_port+"/endpoint";'
#    #insert_text+=f'proxy_set_header    X-Real-IP           $real_ip;'
#    insert_text+=f'proxy_set_header    X-Forwarded-For     $proxy_add_x_forwarded_for;'
#    insert_text+=f'proxy_set_header X-Original-URI $request_uri;'    
#    #insert_text+=f'proxy_set_header    X-Forwarded-Proto   $real_scheme;'
#    insert_text+=f'proxy_set_header    X-Forwarded-Port    $server_port;'
#    insert_text+=f'proxy_set_header    Connection "";'
#    insert_text+=f'proxy_http_version  1.1;'
    insert_text+=f'}}'
    #insert_text+=f'location  /_next {{ proxy_pass {waiting_view_url}/$request_uri; }}'
    #insert_text+=f'location ~* ^/.next {{ rewrite ^/.next(.*)$ /wating/.next$1 last;}}'
    complete_file = fulltext[:check] + insert_text + fulltext[check:]
    print(complete_file)
    return complete_file

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
    complete_file = insert_location(full_text, url)

    save_conf_file(path, complete_file)
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



