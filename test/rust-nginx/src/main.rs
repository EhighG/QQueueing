use std::env;
use std::fs::{self, File};
use std::io::{self, BufRead, BufReader, Write};
use std::os::unix::process;
use std::path::Path;
use std::io::Read;
use regex::Regex;
use glob::glob;

fn merge_files(path: &Path) -> io::Result<String> {
    let nginx_conf_path = path.join("nginx.conf");
    let mut full_text = String::new();

    let file = File::open(nginx_conf_path)?;
    let reader = BufReader::new(file);

    let include_directive = Regex::new(r"^\s*include\s*(.*);").unwrap();

    for line in reader.lines() {
        let line = line?;
        if let Some(caps) = include_directive.captures(&line) {
            if let Some(matched) = caps.get(1) {
                let pattern = matched.as_str();
                for entry in glob(pattern).expect("Failed to read glob pattern") {
                    if let Ok(path) = entry {
                        if let Ok(mut file) = File::open(path) {
                            let mut contents = String::new();
                            file.read_to_string(&mut contents)?;
                            full_text.push_str(&contents);
                        }
                    }
                }
            }
        } else {
            full_text.push_str(&line);
            full_text.push('\n');
        }
    }

    // Remove comments
    let mut cleaned_text = String::new();
    let mut in_comment = false;

    for ch in full_text.chars() {
        if ch == '#' {
            in_comment = true;
        } else if ch == '\n' {
            in_comment = false;
        }
        if !in_comment {
            cleaned_text.push(ch);
        }
    }

    Ok(cleaned_text)
}

fn insert_location(full_text: &str, url: &str) -> String {
    let port = "443";
    dbg!(url);
    let endpoint = url.find('/').map(|index| &url[index..]).unwrap_or("");
    let host = url.split('/').next().unwrap_or("");
    let host_len = host.len();

    let mut is_server = false;
    let mut is_target_port = false;
    let mut is_target_host = false;
    let mut depth = 0;

    let mut location_insert_point = None;

    let mut chars = full_text.char_indices();

    while let Some((i, ch)) = chars.next() {
        //println!("{}", &ch);
        match ch {
            's' if full_text[i..].starts_with("server") && full_text[..i].chars().last().map(|c| c.is_whitespace()).unwrap_or(true) => {
                is_server = true;
            },
            '{' if is_server => {
                depth += 1;
            },
            '}' if is_server => {
                depth -= 1;
                if depth == 0 && is_target_port && is_target_host {
                    location_insert_point = Some(i);
                    break;
                }
            },
            _ if is_server && full_text[i..].starts_with(port) => {
                is_target_port = true;
            },
            _ if is_server && full_text[i..].starts_with(host) => {
                is_target_host = true;
            },
            _ => {}
        }
    }

    let mut complete_file = full_text.to_string();
    if let Some(index) = location_insert_point {
        dbg!(index);
        let waiting_view_url = format!("{}:12344/waiting/1", host);
        let insert_text = format!("location = {} {{ proxy_pass {}; }}", endpoint, waiting_view_url);
        complete_file.insert_str(index, &insert_text);
    }

    complete_file
}


fn main()  {
    let args: Vec<String> = env::args().collect();
    if args.len() < 2 {
        println!("Please input argument");
        std::process::exit(1);
    }
    let url = &args[1];
    println!("{}", args[1]);

    let path = Path::new("/etc/nginx");
    if !path.exists() {
        println!("No path exists");
        std::process::exit(1);
    }

    //let full_text = merge_files(path)?;
    let full_text = merge_files(path);
    let full_text = match merge_files(path) {
        Ok(s) => s,
        _=> std::process::exit(1),
    };
//    for line in full_text.split('\n') {
//        println!("{:?}", line);
//    }

    let complete_file = insert_location(&full_text, url);
//    println!("{}", complete_file); 
//
//    save_conf_file(path, &complete_file)
}
