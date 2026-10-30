"""Lists lambdas in shared (non-client) ParCool classes that touch client-only classes.

Fabric strips @Environment(CLIENT) methods on a dedicated server, but not the synthetic lambda
methods javac generates for lambdas inside them. The JVM verifies every method when a class is
loaded, so such a lambda can make the server load a client class (for example LocalPlayer) and
crash. Usage: client-refs.py <jar>; prints one line per offending lambda.
"""
import re
import subprocess
import sys
import zipfile

CLIENT_PATH = re.compile(r"(/client/|ParCoolClient|ParCoolModMenu|/mixin/)")
CLIENT_REF = re.compile(r"(net/minecraft/client/|com/alrex/parcool/client/)[A-Za-z0-9_/$]+")

jar = sys.argv[1]
classes = [n[:-6].replace("/", ".") for n in zipfile.ZipFile(jar).namelist()
           if n.startswith("com/alrex/parcool/") and n.endswith(".class") and not CLIENT_PATH.search(n)]
found = 0
for i in range(0, len(classes), 200):
    out = subprocess.run(["javap", "-c", "-p", "-cp", jar] + classes[i:i + 200],
                         capture_output=True, text=True).stdout
    cls, method, refs = None, None, set()

    def flush():
        global found
        if method and "lambda$" in method and refs:
            found += 1
            print(f"{cls} {method.split('(')[0].split()[-1]}: {', '.join(sorted(refs))}")

    for line in out.splitlines():
        m = re.match(r"^(?:public |final |abstract |sealed |non-sealed )*(?:class|interface|enum|record) (\S+)", line)
        if m:
            flush(); cls, method, refs = m.group(1), None, set()
        elif re.match(r"^  \S.*\(.*\).*;$", line):
            flush(); method, refs = line.strip(), {r.group(0) for r in CLIENT_REF.finditer(line)}
        elif method:
            refs.update(r.group(0) for r in CLIENT_REF.finditer(line))
    flush()
print(f"{found} lambda(s) in shared classes reference client classes")
