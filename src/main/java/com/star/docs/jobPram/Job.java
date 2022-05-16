package com.star.docs.jobPram;


/**
 * 样例json
 *
 * {
 *   "JobNum": 1,
 *   "job1": {
 *     "source": {
 *       "type": "kafka",
 *       "url": "hadoop102",
 *       "port": "9092",
 *       "topic": "kfkSQL"
 *     },
 *     "operator": {
 *       "num": 2,
 *       "operator1": {
 *         "type": "OpFilt",
 *         "key": "abc"
 *       },
 *       "operator2": {
 *         "type": "OpFilt",
 *         "key": "star"
 *       }
 *     },
 *     "dest": {
 *       "type": "kafka",
 *       "url": "hadoop102",
 *       "port": "9092",
 *       "topic": "kfkPort"
 *     },
 *     "jobTime": "15"
 *   }
 * }
 */

//此类为任务执行参数json说明类，无实际作用
public class Job {
    //子任务个数
    private int JobNum;

    //子任务对象数组： job1，job2，job3......
    private Task[] jobi;

}
