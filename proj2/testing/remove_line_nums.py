import sys

if len(sys.argv) != 2:
    print("please input a file to scrub line numbers");
    sys.exit(1)

try:
    s = ""
    with open(sys.argv[1],'r') as f:
        for line in f.readlines():
            if not line.isspace():
                x = line.find(".")
                if x == -1 or x > 5:
                    print(f"error finding number on this line:\n{line}")
                    sys.exit(1)
                s += line[x+2:]
            else:
                s += line
    if s[-1] != "\n":
        s += "\n" # all test files used by autograder should end in newlines
    with open(sys.argv[1],'w') as f:
        f.write(s)
except FileNotFoundError:
    print("File not found")
    sys.exit(1)
