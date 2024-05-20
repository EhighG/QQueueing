from __future__ import annotations
import re
from typing import Optional, Union, List, Set
import os
import glob
import pickle
import copy


class Block:

    def __init__(self, body: List(Block) = None):
        '''
        params: type,
        return: none
        '''
        self.body = [] if body is None else body

    def push(self, ind: int, val: Block) -> None:
        '''
        params: ind, val
        return: None
        '''
        if ind == -1:
            self.body.append(val)
        else:
            self.body.insert(ind, val)

    def pop(self, ind: int) -> None:
        '''
        params: ind
        return: none
        '''
        del self.body[ind]

    def find_by_type(self, type):
        '''
        match all type in the block
        params: type
        return: [ind]
        '''
        ans = [i for i, body in enumerate(self.body) if body.type == type]
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
        for i, body in enumerate(self.body):
            if body.type == type:
                for key in match[type]:
                    if body[key] == kwargs[key]:
                        return i
        return -1

    def export(self, indent=0):
        '''
        params: none
        return: string
        '''
        conf_string += start
        for body in self.body:
            conf_string += body.export(indent+1)
        conf_string += end
        return conf_string
        
    def __str__(self):
        return f"{self.__class__.__name__}"

    def __repr__(self):
        return f"{self.__class__.__name__}"
    
    def __len__(self) -> int:
        return len(self.body)

    def __getitem__(self, index: int) -> Block:
        return self.body[index]

    def __setitem__(self, index: int, value: Block) -> None:
        self.body[index] = value

    def __iter__(self):
        return iter(self.body)

    @staticmethod
    def parse_string(conf_file: str) -> Block:
        start = -1
        b_nginx = Nginx()
        stack: List(Block) = [b_nginx]
        for i, text in enumerate(conf_file):
            if text == '{':
                check_str = conf_file[start:i].split()
                type = check_str[0]
                if type == 'http':
                    stack.append(Http())
                elif type == 'types':
                    stack.append(Types())
                elif type == 'server':
                    stack.append(Server())
                elif type == 'events':
                    stack.append(Events())
                elif type == 'mail':
                    stack.append(Mail())
                elif type == 'upstream':
                    stack.append(Upstream())
                elif type == 'location':
                    cond = ' '.join(check_str[1:])
                    stack.append(Location(condition=cond))
                elif type == 'if':
                    cond = ' '.join(check_str[1:])
                    stack.append(If(condition=cond))
                start = -1
            elif text == '}':
                close_block = stack.pop()
                stack[-1].push(ind=-1, val=close_block)
                start = -1
            elif text == ';':
                b_statement = Statement(conf_file[start:i+1])
                stack[-1].push(ind=-1, val=b_statement)
                start = -1
            elif start < 0 and text.isalnum():
                start = i  # server, location, types, http, if
        return b_nginx


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

    def save_file(self, path: str, overwrite=True):
        if os.path.exists(path):
            if overwrite:
                os.remove(path)
            else:
                raise FileExistsError()
        with open(path, 'w') as f:
            f.write(self.export())

    def save_pkl(self, path: str, overwrite=True):
        """save string to pickle file
        overwrite true by default.
        """
        if os.path.exists(path):
            if overwrite:
                os.remove(path)
            else:
                raise FileExistsError()
        with open(path, 'wb') as f:
            pickle.dump(obj=self, file=f)
        print('pickle perfectly saved.')
        
    @staticmethod
    def load_pkl(path: str) -> Block:
        """load pickle file
        """
        if not os.path.exists(path):
            raise FileNotFoundError()
        with open(path, 'rb') as f:
            new_block = pickle.load(file=f)
        return new_block


class Statement(Block):
    '''
    Statement has exact one statement.
    Endswith ;
    usage: set, listen, server_name, proxy~~
    Statement has directive part, arguement part.
    Each of them are str type.

    '''
    def __init__(self, body: str = None) -> None:
        if body is None:
            raise Exception("Statement must have one statement.")
        elif not body.endswith(';'):
            raise Exception("Statement must end with ;.")
        # this is only for query
        tmp = body.split()
        self.directive = tmp[0]
        self.arguement = ' '.join(tmp[1:])
        super().__init__(body=[body])

    def push(self, ind: int, val: Block) -> None:
        raise Exception("Statement can not be pushed.")

    def pop(self, ind: int) -> None:
        raise Exception("Statement can not be popped.")

    def export(self, indent: int = 0) -> str:
        return '\t'*indent + self.body[0] + '\n'

    def __len__(self) -> int:
        raise Exception("Statement doesn't have length.")

    def __getitem__(self, index: int) -> Block:
        raise Exception("Statement doesn't have body")

    def __setitem__(self, index: int, value: Block) -> None:
        raise Exception("Statement doesn't have body")

    def __iter__(self):
        raise Exception("Statement doesn't have body")


class If(Block):

    """Docstring for If.
    If has exact one condition.
    The condition is wrapped in parantheses.
    If has List of Statements.
    """
    def __init__(self, condition: str, body: List = None):
        super().__init__(body=body)
        if not ( condition.startswith('(') and condition.endswith(')') ):
            raise Exception("not invalid condition.")
        self.condition = condition
        
    def export(self, indent: int = 0) -> str:
        start = end = '\t' * indent
        string = start + 'if ' + self.condition + ' {\n'
        for item in self.body:
            string += item.export(indent=indent+1)
        string += end + '}\n'
        return string

class Location(Block):

    """Docstring for Location.
    Location has exact one condition.
    Location has List of Statements.
    """
    def __init__(self, condition: str, body: List = None):
        super().__init__(body=body)
        self.condition = condition
        
    def export(self, indent: int = 0) -> str:
        start = end = '\t' * indent
        string = start + 'location ' + self.condition + ' {\n'
        for item in self.body:
            string += item.export(indent=indent+1)
        string += end + '}\n'
        return string
        

class Upstream(Block):

    """Docstring for Upstream.
    Upstream has List of Statements.
    Upstream has name.
    """
    def __init__(self, name: str, body: List(Statement) = None):
        super().__init__(body=body)
        self.name = name

    def export(self, indent: int = 0) -> str:
        start = end = '\t' * indent
        string = start + 'upstream ' + self.name + ' {\n'
        for item in self.body:
            string += item.export(indent=indent+1)
        string += end + '}\n'
        return string


class Server(Block):

    """Docstring for Server.
    Server has List of Statements.
    Server can have ports, hosts.
    """
    def __init__(self, ports: Set(str) = None, hosts: Set(str) = None, body: List(Block) = None):
        super().__init__(body=body)
        self.ports: Set(str) = set() if ports is None else ports
        self.hosts: Set(str) = set() if hosts is None else hosts

        # set default values for easy query
        for item in self.body:
            if isinstance(item, Statement):
                if item.directive == 'listen':
                    for jtem in item.arguement.split():
                        self.ports.add(jtem.strip(';'))
                elif item.directive == 'server_name':
                    for jtem in item.arguement.split():
                        self.hosts.add(jtem.strip(';'))

    def push(self, ind: int, val: Block) -> None:
        if isinstance(val, Statement):
            if val.directive == 'listen':
                for jtem in val.arguement.split():
                    self.ports.add(jtem.strip(';'))
            elif val.directive == 'server_name':
                for jtem in val.arguement.split():
                    self.hosts.add(jtem.strip(';'))
        super().push(ind=ind, val=val)

    def pop(self, ind: int) -> None:
        val: Block = self.body[ind]
        if isinstance(val, Statement):
            if val.directive == 'listen':
                for jtem in val.arguement.split():
                    self.ports.remove(jtem.strip(';'))
            elif val.directive == 'server_name':
                for jtem in val.arguement.split():
                    self.hosts.remove(jtem.strip(';'))
        super().pop(ind=ind)

    def export(self, indent: int = 0) -> str:
        start = end = '\t' * indent
        string = start + 'server ' + '{\n'
        for item in self.body:
            string += item.export(indent=indent+1)
        string += end + '}\n'
        return string


class Types(Block):
    """Docstring for Types.
    Types has List of Statements.
    """
    def __init__(self, body: List(Statement) = None) -> None:
        super().__init__(body=body)

    def export(self, indent: int = 0) -> str:
        start = end = '\t' * indent
        string = start + 'types ' + '{\n'
        for item in self.body:
            string += item.export(indent=indent+1)
        string += end + '}\n'
        return string


class Http(Block):
    """Docstring for Http.
    Http has List of Statements, Types, Server, Upstream.
    """
    def __init__(self, body: List(Block) = None) -> None:
        super().__init__(body=body)

    def export(self, indent: int = 0) -> str:
        start = end = '\t' * indent
        string = start + 'http ' + '{\n'
        for item in self.body:
            string += item.export(indent=indent+1)
        string += end + '}\n'
        return string


class Events(Block):
    """Docstring for Events.
    Events has List of Statements.
    """
    def __init__(self, body: List(Statement) = None) -> None:
        super().__init__(body=body)

    def export(self, indent: int = 0) -> str:
        start = end = '\t' * indent
        string = start + 'events ' + '{\n'
        for item in self.body:
            string += item.export(indent=indent+1)
        string += end + '}\n'
        return string


class Mail(Block):
    """Docstring for Mail.
    Mail has List of Statements.
    """
    def __init__(self, body: List(Statement) = None) -> None:
        super().__init__(body=body)

    def export(self, indent: int = 0) -> str:
        start = end = '\t' * indent
        string = start + 'mail ' + '{\n'
        for item in self.body:
            string += item.export(indent=indent+1) + '\n'
        string += end + '}\n'
        return string


class Nginx(Block):
    '''
    Nginx has exact one Http, Events.
    Events has List of Statements.

    '''
    def __init__(self, body: Optional[List[Union[Http, Events, Statement]]] = None):
        # need to check exact one value inputted
        super().__init__(body=body)
        
    def export(self, indent: int = -1) -> str:
        string = ''
        for item in self.body:
            string += item.export(indent=indent+1) 
        return string






if __name__ == '__main__':

    b_nginx = None
    with open("./tmp.txt", 'r') as f:
        b_nginx = Block.parse_string(f.read())

    #print(b_nginx.export())
    save_file = './nginx.pkl'
    #b_nginx.save_pkl(path=save_file)
    c_nginx = Block.load_pkl(save_file)
    print(c_nginx.export())


#    print(http_block.export())
#    print(nginx_block.bodies)
#    print(nginx_block.find_by_type(type='server'))
