import paramiko, sys, os, subprocess, signal
from collections import OrderedDict
services = [{
    "order": 1,
    "class": "registry.ServerRegisterRemoteObject",
    "machine": "localhost",
    "args": "localhost 4000 3999"
  },
  {
    "order": 2,
    "class": "monitors.GeneralRepository.GeneralRepositoryStart",
    "machine": "localhost",
    "args": "4100 heistsim.log localhost 4000"
  },
  {
    "order": 3,
    "class": "monitors.AssaultParty.AssaultPartyStart",
    "machine": "localhost",
    "args": "4200 0 localhost 4000"
  },
  {
    "order": 4,
    "class": "monitors.AssaultParty.AssaultPartyStart",
    "machine": "localhost",
    "args": "4300 1 localhost 4000"
  },
  {
    "order": 5,
    "class": "monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart",
    "machine": "localhost",
    "args": "4400 localhost 4000"
  },
  {
    "order": 6,
    "class": "monitors.ConcentrationSite.ConcentrationSiteStart",
    "machine": "localhost",
    "args": "4500 localhost 4000"
  },
  {
    "order": 7,
    "class": "monitors.Museum.MuseumStart",
    "machine": "localhost",
    "args": "4600 localhost 4000"
  },
  {
    "order": 8,
    "class": "main.OrdinaryThievesStart",
    "machine": "localhost",
    "args": "localhost 4000"
  },
  {
    "order": 9,
    "class": "main.MasterThiefStart",
    "machine": "localhost",
    "args": "localhost 4000"
  }]

COMMAND = '-cp %s:libs/* %s %s'
COMMAND2 = 'java -Djava.rmi.server.codebase="%s"\
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
    realPath = os.path.realpath(__file__)  # /home/user/test/my_script.py
    dirPath = os.path.dirname(realPath)
    FILELOCATION = dirPath+"/"+FILENAME

    #services = OrderedDict(sorted(services, key=lambda x: x['order']))

    services.sort(key=lambda x:x['order'])

    #iniciar o RMI register, como tme no exemplo do borges
    servicesPID.append(subprocess.Popen("rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false 3999", shell=True).pid)
    

    #iniciar o ServerRegisterRemoteObject dá erro com o register. VER
    for s in services:
        cmd = COMMAND % (FILENAME, s['class'], s['args'])
        try:
          print("Starting... %s" % s['class'])
          servicesPID.append(subprocess.Popen(COMMAND2 % (FILELOCATION, cmd), shell=True).pid)
          break
        except ValueError:
            print(ValueError)
            killServices()
    print('Done')
