import paramiko, sys, os
from collections import OrderedDict
import time

services = [{
    "order": 1,
    "class": "registry.ServerRegisterRemoteObject",
    "machine": "l040101-ws01.ua.pt",
    "args": "l040101-ws01.ua.pt 22190 22191"
  },
  {
    "order": 2,
    "class": "monitors.GeneralRepository.GeneralRepositoryStart",
    "machine": "l040101-ws02.ua.pt",
    "args": "22191 heistsim.log l040101-ws01.ua.pt 22190"
  },
  {
    "order": 3,
    "class": "monitors.AssaultParty.AssaultPartyStart",
    "machine": "l040101-ws03.ua.pt",
    "args": "22191 0 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 4,
    "class": "monitors.AssaultParty.AssaultPartyStart",
    "machine": "l040101-ws04.ua.pt",
    "args": "22191 1 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 5,
    "class": "monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart",
    "machine": "l040101-ws05.ua.pt",
    "args": "22191 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 6,
    "class": "monitors.ConcentrationSite.ConcentrationSiteStart",
    "machine": "l040101-ws06.ua.pt",
    "args": "22191 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 7,
    "class": "monitors.Museum.MuseumStart",
    "machine": "l040101-ws07.ua.pt",
    "args": "22191 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 8,
    "class": "main.OrdinaryThievesStart",
    "machine": "l040101-ws08.ua.pt",
    "args": "l040101-ws01.ua.pt 22190"
  },
  {
    "order": 9,
    "class": "main.MasterThiefStart",
    "machine": "l040101-ws09.ua.pt",
    "args": "l040101-ws01.ua.pt 22190"
  }]

COMMAND = '-cp %s:libs/* %s %s'
                 
COMMAND2 = 'cd Public ; java -Djava.rmi.server.codebase="http://%s/sd0109/%s"\
                -Djava.security.policy=java.policy\
                %s'
                
USERNAME = "sd0109"
PASSWORD = "ricasricas"
LOGFILENAME = None

def sendFileToMachines():
    for s in services:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)
        sftp = ssh.open_sftp()
        sftp.chdir("/home/sd0109/Public")
        sftp.put(os.getcwd() + "/"+FILENAME, FILENAME)
        sftp.put(os.getcwd() + "/java.policy", "java.policy")
        sftp.put(os.getcwd() + "/conf.xml", 'conf.xml')
        print ("Sending the jar to the workstation: " + s['machine'])
    ssh.close()

def execServices():
    global LOGFILENAME
    grService = None
    for s in services:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)
        classname = s['class'].split('.')[-1]
        
        #if classname == 'MasterThiefStart' or classname == 'OrdinaryThievesStart':
		#	cmd = COMMAND % (FILENAME, s['class'], s['args'])
		#	ssh.exec_command('cd Public ; java ' + cmd)
		#	print('cd Public ; java ' + cmd)
		#	continue
        
        if classname == 'ServerRegisterRemoteObject':
            print("Initiating rmi")
            ssh.exec_command('rmiregistry -J-Djava.rmi.server.codebase="http://l040101-ws01.ua.pt/sd0109/%s" -J-Djava.rmi.server.useCodebaseOnly=true 22190' % FILENAME)
            time.sleep(2)
        cmd = COMMAND % (FILENAME, s['class'], s['args'])
        stdin, stdout, stderr = ssh.exec_command(COMMAND2 % (s['machine'],FILENAME,cmd))
        print(COMMAND2 % (s['machine'],FILENAME,cmd))
        time.sleep(10)
        if classname == 'GeneralRepositoryStart':
          grService = stdout.channel
          LOGFILENAME = s['args'].split(' ')[1]
          LOGFILENAME = "HeistToTheMuseum_Log"
          print('Saving log in %s file' % LOGFILENAME)
        print("Executing %s in %s:%s" % (classname, s['machine'], s['args'].split(' ')[0] ))

    print('Waiting for simulation to end...')
    if grService.recv_exit_status() == 0:
      print('Simulating has ended successfuly')
      retrieveLog()
      cleaningMachines()
    ssh.close()

def killServices():
  pass

def retrieveLog():
  print ('Retrieving the logfile')
  ssh.connect(services[1]['machine'], username=USERNAME, password=PASSWORD)
  sftp = ssh.open_sftp()
  sftp.chdir("/home/sd0109/")
  sftp.get(LOGFILENAME, LOGFILENAME)
  ssh.close()

def cleaningMachines():
  for s in services:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)
        stdin, stdout, stderr = ssh.exec_command('rm %s' % FILENAME)
        stdin, stdout, stderr = ssh.exec_command('rm conf.xml')
        stdin, stdout, stderr = ssh.exec_command('rm java.policy')
        print("Removing %s from %s" % (FILENAME, s['machine']))
        classname = s['class'].split('.')[-1]
        if classname == 'GeneralRepositoryStart':
          stdin, stdout, stderr = ssh.exec_command('rm %s' % LOGFILENAME)
          print("Removing %s from %s" % (LOGFILENAME, s['machine']))

if __name__ == '__main__':
    FILENAME = sys.argv[1]
    ssh = paramiko.SSHClient()
    ssh.load_system_host_keys()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    sendFileToMachines()
    execServices()
    print('Done')
