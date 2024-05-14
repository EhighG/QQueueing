import re
import os
import glob
import copy

class Block:

    def __init__(self, type, index=0, bodies=None, ports=None, hosts=None, cond=''):
        '''
        params: type,
        return: none
        '''
        # this can be server, location, if, statement
        self.type = type
        # this is index in super block
        self.index = index
        # this can be list of string(statement), block
        self.bodies = [] if bodies is None else bodies

        self.ports = [] if ports is None else ports
        self.hosts = [] if hosts is None else hosts
        self.cond = cond

        # this differs type by type
#        if self.type == 'server':
#            if not port:
#                raise Exception("no port")
#            self.port = port
#            self.host = host
#        elif self.type == 'location':
#            if not cond:
#                raise Exception("no condition statement")
#            self.cond = cond
#        elif self.type == 'if':
#            if not cond:
#                raise Exception("no condition statement")
#        elif self.type == 'statement':
#            if len(self.bodies) != 1:
#                raise Exception("statement must not have other")
#        elif self.type in ['http', 'types', 'nginx', 'event']:
#            pass
#        else:
#            raise Exception("check your type")
#        print('this is block')

    def push(self, ind, val):
        '''
        params: ind, val
        return: none
        '''
        #print(type(val), val.type, '::', val)
        if not isinstance(val, Block): raise Exception("input Block")
        if ind == -1:
            self.bodies.append(val)
        else:
            self.bodies.insert(ind, val)

    def pop(self, ind):
        '''
        params: ind
        return: none
        '''
        del self.bodies[ind]
        print('this is pop')

    def find_by_type(self, type):
        '''
        match all type in the block
        params: type
        return: [ind]
        '''
        ans = [i for i, body in enumerate(self.bodies) if body.type == type]
        return ans

    def find(self, type, **kwargs):
        '''
        if type is location or if -> find by cond
        if type is server  -> find by name, port
        params: type, word
        return: ind
        '''
        if self.type == 'statement':
            raise Exception("statement does not have any sub")

        match = {
                'server': ['name', 'port'],
                'location': ['cond']
                }
        # how can i handle in searching by name generally?
        if type == 'server':
            try:
                kwargs['port']
            except:
                raise Exception("input port")
        elif type in ['location', 'if']:
            try:
                kwargs['cond']
            except:
                raise Exception("input cond")
        # dup val, and case of server have to be considered
        for i, body in enumerate(self.bodies):
            if body.type == type:
                for key in match[type]:
                    if body[key] == kwargs[key]:
                        return i
        return -1

    def get(self, pos):
        '''
        params: pos
        return: block or string
        '''
        return self.bodies[pos]

    def export(self, indent=0):
        '''
        params: none
        return: string
        '''
        if self.type == 'statement':
            return '\t'*indent + self.bodies[0] + '\n'
        conf_string = ''
        start = end = '\t' * indent
        if self.type == 'server':
            start += 'server {\n'
            end += '}\n'
        elif self.type == 'location':
            start += f'location {self.cond} {{\n'
            end += '}\n'
        elif self.type == 'if':
            start += f'if {self.cond} {{\n'
            end += '}\n'
        elif self.type == 'types':
            start += 'types {\n'
            end += '}\n'
        elif self.type == 'events':
            start += 'events {\n'
            end += '}\n'
        elif self.type == 'http':
            start += 'http {\n'
            end += '}\n'

        conf_string += start
        for body in self.bodies:
            conf_string +=  body.export(indent+1)
        conf_string += end
        return conf_string
        
    def __str__(self):
        if self.type == 'statement':
            return f'{self.bodies[0]}'
        return f'{self.type}'

    def __repr__(self):
        return f'{self.type}'

    # this name should be changed. like parse_file
    @staticmethod
    def file_import(conf_file):
        start = -1
        directive_type = ''

        nginx_block = Block(type='nginx')
        stack = [nginx_block]
        for i, text in enumerate(conf_file):
            if text == '{':
                check_str = conf_file[start:i].split()
                cond = ''
                if check_str[0] in ['http', 'types', 'server', 'events', 'mail', 'upstream']:
                    directive_type = check_str[0]
                elif check_str[0] in ['location', 'if']:
                    directive_type = check_str[0]
                    cond = ' '.join(check_str[1:])
                new_block = Block(type=directive_type, cond=cond)
                #print(check_str[0], '||',cond)
                stack.append(new_block)
                start = -1
            elif text == '}':
                close_block = stack.pop()
                #print('close_block', close_block)
                stack[-1].push(ind=-1, val=close_block)
                start = -1
            elif text == ';':
                statement_block = Block(type='statement', bodies=[conf_file[start:i+1]])
                stack[-1].push(ind=-1, val=statement_block)
                start = -1
            elif start < 0 and text.isalnum():
                start = i  # server, location, types, http, if
        return nginx_block

    def pkl_save(self):
        '''
        file save

        '''
        print('save')

    @staticmethod
    def merge_files(path):
        '''
        merge nginx.conf file.
        remove comments.
        params : path
        return : string
        '''
        # merge files
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
        return del_cmt_str

    @staticmethod
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


if __name__ == '__main__':
    lst = [1, 2]

    # later, i need to erase comments first

    http_block = None
    
    with open("./tmp.txt", 'r') as f:
        http_block = Block.file_import(f.read())
    print(http_block.export())
    nginx_block: Block = http_block.bodies[0]
    print(nginx_block.bodies)
    print(nginx_block.find_by_type(type='server'))

    tmp_block = Block(type='server')
    nginx_block.push(ind=11, val=tmp_block)

    print(http_block.export())
    print(nginx_block.bodies)
    print(nginx_block.find_by_type(type='server'))
