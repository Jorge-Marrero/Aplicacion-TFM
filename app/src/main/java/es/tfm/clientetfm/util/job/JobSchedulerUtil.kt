package es.tfm.clientetfm.util.job

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context


fun scheduleJob(ctx : Context){
        val serviceComponent = ComponentName(ctx, JobRefreshIp::class.java)
        val builder = JobInfo.Builder(1, serviceComponent)
            .setPeriodic(3600000)
            .build()

        val jobScheduler = ctx.getSystemService(JobScheduler::class.java)
        jobScheduler.schedule(builder)
}