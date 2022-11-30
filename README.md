# Kasa TP-Link SDK
This is an (unofficial) Android SDK for interacting with Kasa smart devices and the Kasa API. 

## Usage

The entry point to this sdk is the `KasaDeviceManager` class. Use this class by creating an instance using the empty constructor, calling [authenticate], and then using the instance to create [KasaDevice]s by calling [withDevice]. For example:

```kotlin
val manager = KasaDeviceManager()
manager.authenticate(email, password)
val lamp = manager.withDevice(/* alias = */"Kitchen lamp")!!
```
You can also use the `KasaDeviceManager` to get a list of devices associated with the account. There are two types of device objects the SDK exposes: the device objects as returned from the Kasa API (which have a lot of meaningless information), or a more user-friendly object that only has the alias (user-assigned name of smart device) and functions for turning on and off the device. The currently available functions are (I welcome PRs):

```kotlin
lamp.turnOn()
lamp.turnOff()
lamp.toggle() 

lamp.getIsOn()
lamp.setIsOn(true)
```
The above are all suspending functions, as is `authenticate`, because they use Retrofit to invoke the official API, wap.tplinkcloud.com.

Unfortunately, the last two functions cannot be condensed into `lamp.isOn` with a getter and setter because these functions are suspend, and suspending properties are currently [not supported](https://youtrack.jetbrains.com/issue/KT-15555).

# Credits
This SDK is heavily based on [this](https://github.com/artnc/kasa-widget-android) widget from @artnc

# License
This library is licensed under the Apache 2.0 License. Please credit software@sternbach.org.
