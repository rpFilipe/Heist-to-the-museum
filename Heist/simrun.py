import subprocess, signal, sys, os

services = ['java -cp %s:libs/* Configuration.ConfigurationServer 4999 servers.config',
            'java -cp %s:libs/* monitors.GeneralRepository.GeneralRepositoryStart 5004',
            'java -cp %s:libs/* monitors.AssaultParty.AssaultPartyStart 5000 0 localhost 4999',
            'java -cp %s:libs/* monitors.AssaultParty.AssaultPartyStart 5001 1 localhost 4999',
            'java -cp %s:libs/* monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart 5002 localhost 4999',
            'java -cp %s:libs/* monitors.ConcentrationSite.ConcentrationSiteStart 5003 localhost 4999',
            'java -cp %s:libs/* monitors.Museum.MuseumStart 5005 localhost 4999',
            'java -cp %s:libs/* main.OrdinaryThievesStart localhost 4999',
            'java -cp %s:libs/* main.MasterThiefStart localhost 4999'
            ]

configServerLocation = {'localhost', 4999}
servicesPID = []

def startServices(fname):
    global servicesPID
    for s in services:
        try:
            servicesPID.append(subprocess.Popen(s %(fname), shell=True, stdout=subprocess.PIPE).pid)
        except ValueError:
            print(ValueError)
            killServices()

def killServices():
    global servicesPID
    for pid in servicesPID:
        os.kill(pid, signal.SIGINT)
        print("Service %d killed" % pid)

def signal_handler(signal, frame):
    print("123456798")
    killServices()
    sys.exit(0)
#signal.pause()

if __name__ == '__main__':
    signal.signal(signal.SIGINT, signal_handler)
    fname = sys.argv[1]
    startServices(fname)
    print("Waiting to kill Services")
    input()
    killServices()
    


