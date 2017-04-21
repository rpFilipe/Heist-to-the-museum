import subprocess, signal, sys, os

services = ['java -cp %s:libs/* Configuration.ConfigurationServer 4999 servers.config',
            'java -cp %s:libs/* monitors.AssaultParty.AssaultPartyStart 5000 0',
            'java -cp %s:libs/* monitors.AssaultParty.AssaultPartyStart 5001 1',
            'java -cp %s:libs/* monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart 5002',
            'java -cp %s:libs/* monitors.ConcentrationSite.ConcentrationSiteStart 5003',
            'java -cp %s:libs/* monitors.GeneralRepository.GeneralRepositoryStart 5004',
            'java -cp %s:libs/* monitors.Museum.MuseumStart 5005',
            'java -cp %s:libs/* monitors.main.OrdinaryThievesStart localhost 4999',
            'java -cp %s:libs/* monitors.main.MasterThiefStart localhost 4999'
            ]
servicesPID = []

def startServices(fname):
    global servicesPID
    for s in services:
        try:
            servicesPID.append(subprocess.Popen(s %(fname), shell=True).pid)
        except ValueError:
            print(ValueError)
            killServices()

def killServices():
    global servicesPID
    for pid in servicesPID:
        os.kill(pid, signal.SIGINT)
        print("Service %d killed" % pid)


if __name__ == '__main__':
    fname = sys.argv[1]
    startServices(fname)
    print("Waiting to kill Services")
    imput()
    killServices()
    


