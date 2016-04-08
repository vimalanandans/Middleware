# Zbar Scannar
This a Android library project wrapping over the Zbar Bar Code reader project https://github.com/ZBar/ZBar.
It provides a very easy to use interface for scanning and barcodes.

# Setup
## Using with gradle

Inside build.gradle file for the project, add this library as a dependency. For instance, if using from the Android-Build project

```
dependencies {
    compile project(':libraries:zbarscanner')
}
```

Including the project in settings.gradle

```
include ':libraries:zbarscanner'
```

# Usage
### No Data {No data passed to ScannerActivity and back}
*Request for initiating the scan*

```java
Intent intent = new Intent(this, ScannerActivity.class);
startActivityForResult(intent, ScannerActivity.REQUEST_CODE);
```

*Handle the response, implement the 'onActivityResult' method like this*

```java
 @Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == ScannerActivity.REQUEST_CODE){
        if(resultCode == RESULT_OK){
            //do something with the data
            Toast.makeText(this,"Data received"+data.getStringExtra(ScannerActivity.DATA),
            Toast.LENGTH_LONG).show();
        }
    }
}
```
### With Data {Data passed to ScannerActivity and back}
This is useful if you need to pass some data regarding the scan itself. Example, scan triggered for what operation, when, etc. The bundle would returned back as is in the 'onActivityResult'

*Request for initiating the scan*

```java
Intent intent = new Intent(this, ScannerActivity.class);
Bundle bundle = new Bundle();
bundle.putString("KEY", "VALUE");
intent.putExtras(bundle);
startActivityForResult(intent, ScannerActivity.REQUEST_CODE);
```
*Handle the response, implement the 'onActivityResult' method like this*

```java
 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ScannerActivity.REQUEST_CODE){
            if(resultCode == RESULT_OK){
                //do something with the data
                Toast.makeText(this,"Data received"+data.getStringExtra(ScannerActivity.DATA),
                Toast.LENGTH_LONG).show();
                Bundle bundle = data.getExtras();
                Log.d(TAG, "Getting value passed to bundle: "+bundle.getString("KEY"));
            }
        }
    }
```
# TODO
* Improving cleanup of resources like camera, views, etc.
* Using Android lifecycle methods more efficiently.
* Sending back RESULT_CANCELLED (with bundle) when scan failed/cancelled.

# Contact
For any queries regarding bugs, improvements, feature requests, please contact: 

**Rishabh Gulati** *[rishabh.gulati@us.bosch.com]*
