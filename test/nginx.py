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


def insert_location(fulltext, url):
    port = 443
    host = url
    endpoint = ''
    for i,val  in enumerate(url):
        if val == '/':
            host = url[:i]
            endpoint = url[i:]
            break
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

    if check < -1:
        print('check your url is valid')
        exit(1)
    waiting_view_url = host + ':12344/waiting/1'
    insert_text = f'location = {endpoint} {{ proxy_pass {waiting_view_url}; }}'
    complete_file = fulltext[:check] + insert_text + fulltext[check:]
    return complete_file

def save_conf_file(path, complete_file):
    save_path = path+'/complete.conf'
    if os.path.exists(save_path):
        os.remove(save_path)
    with open(save_path, 'w') as f:
        f.write(complete_file)

def main(url):
    path = '/etc/nginx'
    if not os.path.exists(path):
        print('no path exitst')
        exit(1)
    full_text = merge_files(path)
    complete_file = insert_location(full_text, url)

    save_conf_file(path, complete_file)

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print('please input arguement')
        exit(1)
    url = sys.argv[1]
    main(url)



