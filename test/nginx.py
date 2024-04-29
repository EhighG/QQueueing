import re
import os
import glob


class NGINX:

    def __init__(self, conf_path, dirpath):
        self.dirpath = dirpath
        self.conf_path = conf_path
        self.save_path = '/tmp/nginx.conf'

        self.merge_conf()

    def merge_conf(self):
        conf_dir = os.path.dirname(self.conf_path)

        if len(conf_dir) != 0:
            os.chdir(conf_dir)
        
        include_directives = '^\s*include\s*(.*);'
        self.tmptext=''
        with open(self.conf_path, 'r') as f:
            for line in f.readlines():
                included = re.findall(include_directives, line)
                if len(included) > 0:
                    include_files = glob.glob(included[0])
                    for file in include_files:
                        if os.path.exists(file):
                            with open(file, 'r') as ff:
                                include_con = ff.read()
                                self.tmptext += include_con
                else:
                    self.tmptext += line

        self.fulltext = ''

        # remove comments
        flag = True
        self.fulltext=''
        for i,text in enumerate(self.tmptext):
            if text == '#':
                flag = False
            elif text == '\n':
                flag = True
            
            if flag:
                self.fulltext += text


    def insert_location(self, url, port):
        count = 0
        is_server = False
        is_target = False
        check = 0
        for i, char in enumerate(self.fulltext):
            # this has risks to cause error
            if self.fulltext[i:i+6] == 'server':
                if self.fulltext[i-1].isspace() and self.fulltext[i+6].isspace() :
                    is_server = True
            if is_server:
                if char == '{':
                    count += 1
                elif char == '}':
                    count -= 1
                    if not count and is_target:
                        check = i
                        is_server = False
                        is_target = False
                elif self.fulltext[i:i+3] == str(port):
                    is_target = True
        self.fulltext = self.fulltext[:check] + 'location { proxy_pass ' + url + '; }' + self.fulltext[check:]
            

    def save_conf_file(self):
        with open(self.save_path, 'w') as f:
            f.write(self.fulltext)




if __name__ == '__main__':
    conf_path='/etc/nginx/nginx.conf'
    dir_path='/etc/nginx'
    nginx = NGINX(conf_path, dir_path)
    nginx.insert_location("test.com", 443)
    print(nginx.fulltext)
    nginx.save_conf_file()
