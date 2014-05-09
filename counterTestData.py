#!/usr/bin/python
import subprocess
import argparse

exp1 = [11,12,5,1,3,3,3822,0.24,0.04,0.96]
exp2 = [12,10,1,3,3,1,2644,0.11,0.09,0.92]
exp3 = [12,10,4,3,6,2,1304,0.10,0.03,0.90]
exp4 = [14,10,5,5,6,2,315,0.08,0.05,0.90]
exp5 = [15,14,9,16,7,10,4007,0.02,0.10,0.84]
exp6 = [15,15,9,10,9,9,7125,0.01,0.2,0.77]
exp7 = [15,15,10,13,8,10,5328, 0.04, 0.18,0.80]
exp8 = [16,14,15,12,9,5,8840, 0.04, 0.19, 0.76]
allExp = [exp1, exp2, exp3, exp4, exp5, exp6, exp7, exp8]

def runCommand(numMilliseconds=2000, numSources=2, experiment = exp1, className = None):    
    args = ['java',className,str(numMilliseconds)]
    exp = map(str, experiment)
    args.extend(exp)
    args.append(str(numSources))
    print args
    p1 = subprocess.Popen(args, stdout=subprocess.PIPE)
    return p1.communicate()[0]
    
def main():
    global allExp
    parser = argparse.ArgumentParser()
    parser.add_argument("-o","--outfile", type=str, help="output file name")
    parser.add_argument("-n","--numSources", nargs ="+", help="number of sources/threads")
    parser.add_argument("-c", "--className", type=str, help="class that need to run exp")
    parser.add_argument("-e", "--experiment", type=int, nargs ='*', help="lock use for experiment")
    args = parser.parse_args()
    result = ""
    e = args.experiment
    n = args.numSources #[1,2,4,8,12]
    if not e:
        #l = ["TASLock", "BackoffLock", "ReentrantLock", "ALock", "CLHLock", "MCSLockg"]
        e = [0,1,2,3,4,5,6,7]
    for exp in e:
        for i in n:
            result += "Experiment {0}\n".format(str(exp))
            result += runCommand(numSources = str(i),className=args.className, experiment = allExp[exp])
            result += "\n"
    with open("{0}.txt".format(str(args.className) + "_" + str(args.outfile)), "w") as f:
        f.write(result)

if __name__ == "__main__":
    main()
                
        
