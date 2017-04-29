import paramiko, sys, os

simulationParameters = ["NR,5",
                        "NOT,6",
                        "MAPPR,16",
                        "MIPPR,8",
                        "MARD,30",
                        "MIRD,15",
                        "APS,3",
                        "MD,7",
                        "MATS,6",
                        "MITS,2",
                        "MDBT,5"]

services = [{
    "order": 1,
    "class": "Configuration.ConfigurationServer",
    "machine": "l040101-ws01.ua.pt",
    "args": "22190 servers.config"
  },
  {
    "order": 2,
    "class": "monitors.GeneralRepository.GeneralRepositoryStart",
    "machine": "l040101-ws01.ua.pt",
    "args": "22191 heistsim.log l040101-ws01.ua.pt 22190"
  },
  {
    "order": 3,
    "class": "monitors.AssaultParty.AssaultPartyStart",
    "machine": "l040101-ws03.ua.pt",
    "args": "22190 0 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 4,
    "class": "monitors.AssaultParty.AssaultPartyStart",
    "machine": "l040101-ws03.ua.pt",
    "args": "22101 1 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 5,
    "class": "monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart",
    "machine": "l040101-ws04.ua.pt",
    "args": "22190 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 6,
    "class": "monitors.ConcentrationSite.ConcentrationSiteStart",
    "machine": "l040101-ws05.ua.pt",
    "args": "22190 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 7,
    "class": "monitors.Museum.MuseumStart",
    "machine": "l040101-ws07.ua.pt",
    "args": "22190 l040101-ws01.ua.pt 22190"
  },
  {
    "order": 8,
    "class": "main.OrdinaryThievesStart",
    "machine": "l040101-ws09.ua.pt",
    "args": "l040101-ws01.ua.pt 22190"
  },
  {
    "order": 9,
    "class": "main.MasterThiefStart",
    "machine": "l040101-ws10.ua.pt",
    "args": "l040101-ws01.ua.pt 22190"
  }]

COMMAND = 'java -cp %s:libs/* %s %s'
USERNAME = "sd0109"
PASSWORD = "ricasricas"
LOGFILENAME = None

def sendFileToMachines():
    for s in services:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)
        sftp = ssh.open_sftp()
        sftp.chdir("/home/sd0109/")
        sftp.put(os.getcwd() + "/"+FILENAME, FILENAME)
        if s['class'] == 'Configuration.ConfigurationServer':
          sftp.put(os.getcwd() + "/"+'servers.config', 'servers.config')
        print ("Sending the jar to the workstation: " + s['machine'])
    ssh.close()

def execServices():
    global LOGFILENAME
    grService = None
    for s in services:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)
        stdin, stdout, stderr = ssh.exec_command(COMMAND % ( FILENAME, s['class'], s['args']))
        classname = s['class'].split('.')[-1]
        if classname == 'GeneralRepositoryStart':
          grService = stdout.channel
          LOGFILENAME = s['args'].split(' ')[1]
          print('Saving log in %s file' % LOGFILENAME)
        print("Executing %s in %s:%s" % (classname, s['machine'], s['args'].split(' ')[0] ))
        #ssh.exec_command('java -cp Heist-1.0-SNAPSHOT.jar:libs/* Configuration.ConfigurationServer 4999 servers.config')

    print('Waiting for simulation to end...')
    if grService.recv_exit_status() == 0:
      print('Simulating has ended successfuly')
      retrieveLog()
      cleaningMachines()
    ssh.close()

def killServices():
  pass

def generateServersConfig():
    f = open('servers.config', 'w')
    i=0
    for s in services:
      name = s['class'].split('.')[-1][:-5]
      if name =='AssaultParty':
        name = name + str(i)
        i+=1
      if name == 'OrdinaryThieves' or name == 'MasterThief' or name == 'ConfigurationS':
        continue
      location = s['machine']
      port = s['args'].split(' ')[0]
      f.write("%s,%s,%s\n" % (name, location, port))
    f.write("SimulationParameters:\n")

    for p in simulationParameters:
      f.write(p + '\n')
    f.close()

def retrieveLog():
  print ('Retrivieng the logfile')
  ssh.connect(services[1]['machine'], username=USERNAME, password=PASSWORD)
  sftp = ssh.open_sftp()
  sftp.chdir("/home/sd0109/")
  sftp.get(LOGFILENAME, LOGFILENAME)
  ssh.close()

def cleaningMachines():
  for s in services:
        ssh.connect(s['machine'], username=USERNAME, password=PASSWORD)
        stdin, stdout, stderr = ssh.exec_command('rm %s' % FILENAME)
        print("Removing %s from %s" % (FILENAME, s['machine']))
        classname = s['class'].split('.')[-1]
        if classname == 'GeneralRepositoryStart':
          stdin, stdout, stderr = ssh.exec_command('rm %s' % LOGFILENAME)
          print("Removing %s from %s" % (LOGFILENAME, s['machine']))
        elif classname == 'ConfigurationServer':
          stdin, stdout, stderr = ssh.exec_command('rm servers.config')
          print("Removing %s from %s" % ('servers.config', s['machine']))
        #ssh.exec_command('java -cp Heist-1.0-SNAPSHOT.jar:libs/* Configuration.ConfigurationServer 4999 servers.config')

if __name__ == '__main__':
    FILENAME = sys.argv[1]
    #global services
    #services = sorted(services, key=lambda services: services["class"]["order"])
    ssh = paramiko.SSHClient()
    ssh.load_system_host_keys()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    generateServersConfig()
    sendFileToMachines()
    execServices()
    print('Done')
    
    #sftp = ssh.open_sftp()
    #sftp.chdir("/home/sd0109/")
    #print (sftp.listdir())

    #sftp.put(os.getcwd() + "/"+fname, fname)
    #print ("Sending the proper jar to the workstation")
    #print (sftp.listdir())
    #ssh.exec_command('java -cp Heist-1.0-SNAPSHOT.jar:libs/* Configuration.ConfigurationServer 4999 servers.config')
    #ssh.close()
