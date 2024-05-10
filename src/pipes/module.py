class Block:
    def __init__(self, name='none'):
        '''
        params:
        return: 
        '''
        self.body = []
        print('this is block')

    def push(self, pos, val):
        print('this is push')

    def pop(self, pos):
        print('this is pop')

    def find(self, word):
        print('this is find')

    def get(self, pos):
        print('this is find')

    def export(self):
        print('this is export')


if __name__ == '__main__':
    print('this is good')
    block = Block('dd')
