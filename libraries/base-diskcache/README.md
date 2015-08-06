# base-diskcache


[DiskLruCache](https://github.com/JakeWharton/DiskLruCache)属于目前最好的Disk Cache库了，但是由于其的存取API，并不是特别好用。

[ASimpleCache](https://github.com/yangfuhai/ASimpleCache) 提供的API属于比较好用的了。

于是萌生想法，对于其公开的API进行扩展，对外除了原有的存取方式以外，提供类似ASimpleCache那样比较简单的API用于存储，而内部的核心实现，依然是DiskLruCache原本的。

## 方法


### 存

```java
put(String key, Bitmap bitmap)

put(String key, byte[] value)

put(String key, String value)

put(String key, JSONObject jsonObject)

put(String key, JSONArray jsonArray)

put(String key, Serializable value)

put(String key, Drawable value)

editor(String key).newOutputStream(0);//原有的方式
```

### 取

```java

String getAsString(String key);

JSONObject getAsJson(String key)

JSONArray getAsJSONArray(String key)

<T> T getAsSerializable(String key)

Bitmap getAsBitmap(String key)

byte[] getAsBytes(String key)

Drawable getAsDrawable(String key)

InputStream get(String key);//原有的用法

```

## 简单测试
```java
public class CacheTest extends AndroidTestCase
{
    DiskLruCacheHelper helper;
    private static final String TAG = "CacheTest";

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        Log.e(TAG, "setUp");
        helper = new DiskLruCacheHelper(getContext());
    }


    public void testString() throws IOException
    {
        helper.put("testString", "张鸿洋");
        assertEquals("张鸿洋", helper.getAsString("testString"));
    }

    public void testGetStringWithoutVal() throws IOException
    {
        assertEquals(null, helper.getAsString("zhy------zzzzz"));
    }

    public void testJson()
    {
        try
        {
            JSONObject jsonObject = new JSONObject("{\"name\":\"zhy\"}");
            helper.put("testJson", jsonObject);
            assertEquals(jsonObject.toString(), helper.getAsJson("testJson").toString());
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void testSerializable()
    {
        User u = new User();
        u.name = "张鸿洋";
        helper.put("testSerializable", u);
        User u2 = helper.getAsSerializable("testSerializable");
        assertEquals(u.name, u2.name);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void testBitmap()
    {
        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
        helper.put("testBitmap", bm);
        Bitmap bm2 = helper.getAsBitmap("testBitmap");

        assertEquals(bm.getByteCount(), bm2.getByteCount());
    }

    public void testDrawable()
    {
        Drawable d = getContext().getResources().getDrawable(R.mipmap.ic_launcher);
        helper.put("testDrawable", d);
        Drawable d2 = helper.getAsDrawable("testDrawable");
        assertNotNull(d2);
    }


    private static class User implements Serializable
    {
        String name;
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        Log.e(TAG, "tearDown");
        helper.close();
    }


}
```

## 关于我



* [博客](http://blog.csdn.net/lmj623565791)
* [新浪微博](http://weibo.com/u/3165018720)
* [Android教学视频](http://www.imooc.com/space/teacher/id/320852)
* [email:623565791@qq.com](mailto:623565791@qq.com)
