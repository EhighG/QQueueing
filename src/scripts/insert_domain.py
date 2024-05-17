import sys


def main(url: str):
    yaml_path = "./src/build/main/application.yml"
    env_path = "./src/frontend/.env"

    yaml_string = ''
    with open(yaml_path, 'r') as f:
        server_flag = False
        for line in f.readlines():
            if line.startswith('server:'):
                server_flag = True
            if server_flag and line.strip().startswith('main:'):
                yaml_string += f'  main: {url}\n'
                server_flag = False
            else:
                yaml_string += line
    
    with open(yaml_path, 'w') as f:
        f.write(yaml_string)
    
    print("yaml config done..")
    
    env_string = ''
    with open(env_path, 'r') as f:
        for line in f.readlines():
            if line.startswith('NEXT_PUBLIC'):
                if line.startswith('NEXT_PUBLIC_TARGET_URL'):
                    env_string += line
                else:
                    key = line.split('=')[0]
                    env_string += f'{key}={url}/qqueueingAPI\n'
            else:
                env_string += line
    with open(env_path, 'w') as f:
        f.write(env_string)

    print('env config done..')
    


    return None


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise IOError("Insert arguement.")
    domain = sys.argv[1]
    print("domain configuration started...")
    main(domain)
