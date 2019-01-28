# Dengue App - Android

## Table of conetnt
* [Getting Started](#sec-1)
* [Usage](#sec-2)
* [Develop](#sec-3)
* [Issue](#sec-4)
* [License](#sec-5)


<a name='sec-1'></a>
## Getting Started
### Prerequisite
* Android Studio (version < 3.0)
	* You can find it in [Android Studio download archives](https://developer.android.com/studio/archive) 

<a name='sec-2'></a>
## Usage
* Please check [The Mosquito Man App Instruction](./doc/掌蚊人APP.pdf)

<a name='sec-3'></a>
## Develop
### Structure
Structured according to [android studio projects Overview](https://developer.android.com/studio/projects/index.html)

* **Breeding Source** (孳生源回報)
* **Drugbite** (蚊子咬回報)
* **Guild** (導覽畫面)
* **Report** (回報審核點清單)
* **User** (使用者相關)
* **Hospital** (醫院清單)
* **Hot** (熱區 web view)
* **Session** (Use to save data.)
    * `setData(String key, String value)`
        * `key`: key of data
        * `value`: value of data
    * `getData(String key)`
        * `key`: key of data
        * It will return `value`.
    * Example  
		
		```android
		string AppName = "dengue";
		session Session = new session(getSharedPreferences(AppName, 0));
		
		session.setData("isLogin", "true");
		// it will save isLogin as "true".
		session.getData("isLogin");
		// it will return "true"
		```

* **GPS** (Use to get address name.)
    * `get(double lat, double lon)`
        * `lat`: double type
        * `lon`: double type
    * Example

		```android
		// "this" is android activity.
		gps Gps = new Gps(this);
		Gps.get(0.0, 0.0);
		// it will return a name of [0, 0]
		```

<a name='sec-4'></a>
## Issue
* [ ] 未來要修改介面的話，在未登入都情況下按下個人設定按鈕，再按註冊下面的小字進入登入頁面，如果使用者他其實是未註冊的，現在想要退回去可能不知道怎麼操作比較好
* [ ] By the time of 2018/11/01, Google Play no longer support API level lower than 26. Thus, this app might not be able to be published on Google Play.

<a name='sec-5'></a>
## License
Copyright (c) NCKU The Mosquito Man Project. All rights reserved.

Licensed under the MIT License.
