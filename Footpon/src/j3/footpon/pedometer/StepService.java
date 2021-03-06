package j3.footpon.pedometer;

import j3.footpon.FootponMapActivity;
import j3.footpon.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class StepService extends Service implements StepListener{
	
	int steps;
	float points;
	
	private SharedPreferences state;
    private SharedPreferences.Editor stateEditor;
	
	StepDetector stepDetector;
	SensorManager sensorManager;
	StepBinder binder = new StepBinder();
	StepDisplayer stepDisplayer;
	
	private WakeLock wakeLock;
	private NotificationManager notificationManager;
	
	public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}
	
	@Override
    public void onCreate() {
        super.onCreate();
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepService");
        wakeLock.acquire();
        
        state = getSharedPreferences("state", 0);
        points = state.getFloat("points", 0);
        steps = state.getInt("steps", 0);
        
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        Log.d(SENSOR_SERVICE, "Created StepService...");
        stepDetector = new StepDetector();
        stepDetector.addStepListener(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(sensorManager.SENSOR_ACCELEROMETER);
        sensorManager.registerListener(stepDetector,sensor,sensorManager.SENSOR_DELAY_FASTEST);
        
	}	
	
	public void registerListener(StepListener listener){
		if(stepDetector != null){
			stepDetector.addStepListener(listener);
		}
	}
	
	@Override
	public void onDestroy(){
		sensorManager.unregisterListener(stepDetector);
		
		wakeLock.release();
		
		stateEditor = state.edit();
	    stateEditor.putInt("steps", steps);
	    stateEditor.putFloat("points", points);
	    stateEditor.commit();
		
		notificationManager.cancel(R.string.app_name);
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        // Tell the user we started.
        Toast.makeText(this, "Started pedometer", Toast.LENGTH_SHORT).show();
    }
	
	/**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        CharSequence text = getText(R.string.app_name);
        Notification notification = new Notification(R.drawable.icon, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, FootponMapActivity.class), 0);
        notification.setLatestEventInfo(this, text,
                getText(R.string.app_name), contentIntent);

        notificationManager.notify(R.string.app_name, notification);
    }
    
    public void setStepDisplayer(StepDisplayer displayer){
    	stepDisplayer = displayer;
    	displayer.passValue(steps, points);
    }
    
	@Override
	public void onStep() {
		points += 0.25f;
		steps += 1;
		if(stepDisplayer != null){
			stepDisplayer.passValue(steps, points);
		}
	}

}
