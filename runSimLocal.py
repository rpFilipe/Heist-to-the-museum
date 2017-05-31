import paramiko, sys, os, subprocess, signal
from collections import OrderedDict
import time

services = [{
    "order": 1,
    "class": "registry.ServerRegisterRemoteObject",
    "machine": "localhost",
    "args": "localhost 22110 22109"
  },
  {
    "order": 2,
    "class": "monitors.GeneralRepository.GeneralRepositoryStart",
    "machine": "localhost",
    "args": "4100 heistsim.log localhost 22110"
  },
  {
    "order": 3,
    "class": "monitors.AssaultParty.AssaultPartyStart",
    "machine": "localhost",
    "args": "4200 0 localhost 22110"
  },
  {
    "order": 4,
    "class": "monitors.AssaultParty.AssaultPartyStart",
    "machine": "localhost",
    "args": "4300 1 localhost 22110"
  },
  {
    "order": 5,
    "class": "monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart",
    "machine": "localhost",
    "args": "4400 localhost 22110"
  },
  {
    "order": 6,
    "class": "monitors.ConcentrationSite.ConcentrationSiteStart",
    "machine": "localhost",
    "args": "4500 localhost 22110"
  },
  {
    "order": 7,
    "class": "monitors.Museum.MuseumStart",
    "machine": "localhost",
    "args": "4600 localhost 22110"
  },
  {
    "order": 8,
    "class": "main.OrdinaryThievesStart",
    "machine": "localhost",
    "args": "localhost 22110"
  },
  {
    "order": 9,
    "class": "main.MasterThiefStart",
    "machine": "localhost",
    "args": "localhost 22110"
  }]

COMMAND = '-cp %s:libs/* %s %s'
COMMAND2 = 'java -Djava.rmi.server.codebase="file://$(pwd)/"\
                 -Djava.security.policy=java.policy\
                 %s'
USERNAME = "sd0109"
PASSWORD = "ricasricas"
FILELOCATION = None
LOGFILENAME = None
servicesPID = []

def killServices():
    global servicesPID
    for pid in servicesPID:
        os.kill(pid, signal.SIGINT)
        print("Service %d killed" % pid)

def signal_handler(signal):
    print("123456798")
    killServices()
    sys.exit(0)

if __name__ == '__main__':
    FILENAME = sys.argv[1]

    #services = OrderedDict(sorted(services, key=lambda x: x['order']))

    #p = services.sort(key=lambda x:x['order'])
    #p = sorted(services)
    #iniciar o RMI register, como tme no exemplo do borges
    servicesPID.append(subprocess.Popen("rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false 22110", shell=True).pid)
    for s in services:
        print("Starting... %s" % s['class'])
        cmd = COMMAND % (FILENAME, s['class'], s['args'])
        try:
          #servicesPID.append(subprocess.Popen("./registry-servers.sh %s"% s['class'], shell=True).pid)
          servicesPID.append(subprocess.Popen(COMMAND2 % cmd, shell=True).pid)
          time.sleep(5)
        except ValueError:
            print(ValueError)
            killServices()
    print('Done')
