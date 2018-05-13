import numpy as np
import cv2

import os
import subprocess
import json

repeats = 1

threads = [1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024]

directory = 'images/'

images = []

for dirpath,_,filenames in os.walk(directory):
    for f in filenames:
        if f.endswith('.jpg') and 'size' in f:
            images.append(os.path.abspath(os.path.join(dirpath, f)))

images.sort()

# java_comand = "/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/bin/java -Dfile.encoding=UTF-8 -classpath /Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/jaccess.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/jfxswt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/lib/packager.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/lib/tools.jar:/Users/LucaAngioloni/Desktop/ParallelComputingExam/HistogramEQParallel/out/production/HistogramEQParallel:/Users/LucaAngioloni/Desktop/ParallelComputingExam/HistogramEQParallel/commons-cli-1.3.1.jar it.LucaAngioloni.Main -json"

java_comand = "java -classpath ../HistogramEQParallel/out/production/HistogramEQParallel:../HistogramEQParallel/commons-cli-1.3.1.jar it.LucaAngioloni.Main -json"

serial_results = np.zeros((repeats,len(images)))
parallel_results = np.zeros((repeats,len(images), len(threads)))

j = 0
for image in images:
    for i in range(repeats):
        com_serial = java_comand + " -p " + image +" -s"
        process = subprocess.Popen(com_serial.split(), stdout=subprocess.PIPE)
        output, error = process.communicate()
        output = output.decode("utf-8")
        json_data = json.loads(output)
        serial_results[i,j] = json_data["time_serial"]

        t = 0
        for thread in threads:
            com_parallel = java_comand + " -p " + image + " -t " + str(thread) + " -par"
            process = subprocess.Popen(com_parallel.split(), stdout=subprocess.PIPE)
            output, error = process.communicate()
            output = output.decode("utf-8")
            json_data = json.loads(output)
            parallel_results[i,j,t] = json_data["time_parallel"]
            t = t+1
        j = j+1

print(serial_results)
print(parallel_results)