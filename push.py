import os
import sys

arg = sys.argv[1]
if arg is not None:
    os.system("git add --all")
    os.system("git commit -am "+arg)
    os.system("git push origin master")
