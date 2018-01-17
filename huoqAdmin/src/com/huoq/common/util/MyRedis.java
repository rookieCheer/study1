package com.huoq.common.util;

import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author 作者: jbb E-mail:jbbdx9900@126.com
 * @version 创建时间：2012-4-24 上午10:08:18
 * <p>
 * 类说明: Redis的访问类
 */
public class MyRedis {
    static Logger log = Logger.getLogger(MyRedis.class);
    private static JedisPool pool;

    private static String redis_pwd = (String) PropertiesUtil.getProperties("redis_pwd");
    private static String redis_ip = (String) PropertiesUtil.getProperties("redis_ip");
    private static int port = Integer.parseInt(PropertiesUtil.getProperties("port").toString());
    private static int MaxActive = Integer.parseInt(PropertiesUtil.getProperties("MaxActive").toString());
    private static int MaxIdle = Integer.parseInt(PropertiesUtil.getProperties("MaxIdle").toString());
    private static int MaxWait = Integer.valueOf(PropertiesUtil.getProperties("MaxWait").toString());
    private static boolean TestOnBorrow = Boolean.valueOf(PropertiesUtil.getProperties("TestOnBorrow").toString());
    private static int dbIndex = Integer.valueOf(PropertiesUtil.getProperties("dbIndex").toString());

    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MaxActive);
        config.setMaxIdle(MaxIdle);
        config.setMaxWaitMillis(MaxWait);
        config.setTestOnBorrow(TestOnBorrow);

        pool = new JedisPool(config, redis_ip, port, MaxWait, redis_pwd, dbIndex);
    }

    /**
     * getJedis的链接池对象
     *
     * @param key 要判断的key
     * @return
     */
    private Jedis getJedis() {

        Jedis jedis = pool.getResource();

        return jedis;
    }

    /**
     * 储存某个Key和值并设置超时时间
     *
     * @param key   存储的key
     * @param time  超时时间(秒)
     * @param value 存储的值
     * @return
     */
    public boolean setex(String Key, int time, String value) {
        if (value == null)
            return false;
        Jedis jedis = null;
        try {
            jedis = getJedis();

            jedis.setex(Key, time, value);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 储存摸个Key和值
     *
     * @param key   存储的key
     * @param time  超时时间(秒)
     * @param value 存储的值
     * @return
     */
    public boolean set(String Key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(Key, value);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 储存摸个Key和对象
     *
     * @param key   存储的key
     * @param value 存储的值
     * @return
     */
    public boolean set(String Key, byte[] b) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(Key.getBytes(), b);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 储存摸个Key和map 到一个hash中
     *
     * @param key 存储的key
     * @param Map 存储的值
     * @return
     */
    public boolean setHash(String key, Map<String, String> map) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hmset(key, map);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 储存一个对象到Sorted-Sets
     *
     * @param key 存储的key
     * @param Map 存储的值
     * @return
     */
    public boolean zadd(String key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Long end = jedis.zadd(key, score, member);
            log.info("end zadd:" + end);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 排序取出Sorted-Sets 小到大
     *
     * @param key 存储的key
     * @param Map 存储的值
     * @return
     */
    public Set<Tuple> zrevrangeWithScores(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            Set<Tuple> ss = jedis.zrevrangeWithScores(key, start, end);
            return ss;
        } catch (Exception e) {
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 排序取出Sorted-Sets 大到小
     *
     * @param key 存储的key
     * @param Map 存储的值
     * @return
     */
    public Set<Tuple> zrangeWithScores(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Set<Tuple> ss = jedis.zrangeWithScores(key, start, end);
            return ss;
        } catch (Exception e) {
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 读取某个key的value
     *
     * @param key 存储的key
     * @return
     */
    public String get(String Key) {
        String end = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            end = jedis.get(Key);
            return end;
        } catch (Exception e) {
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 读取某个hmget key的value
     *
     * @param key 存储的key
     * @return
     */
    public String hget(String Key, String Keyin) {
        String end = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();

            end = jedis.hget(Key, Keyin);
            return end;
        } catch (Exception e) {
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 从Sorted-Sets 中删除某个值
     *
     * @param key   Sorted-Sets的key
     * @param Keyin 要删除的对象
     * @return
     */
    public String zrem(String Key, String Keyin) {

        Jedis jedis = null;
        try {
            jedis = getJedis();
            return "0";
        } catch (Exception e) {
            return "-1";
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 删除某个key
     *
     * @param key 要判断的key
     * @return
     */
    public boolean del(String Key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.del(Key);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 判断某个key是否存在
     *
     * @param key 要判断的key
     * @return
     */
    public boolean exists(String key) {
        boolean boo = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            boo = jedis.exists(key);
            return boo;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 判断某个key的过去时间（秒）
     *
     * @param key 要判断的key
     * @return
     */
    public Long existsttl(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Long i = jedis.ttl(key);
            return i;
        } catch (Exception e) {
            return -1l;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 储存某个Key和值 以List的形式 并删除游标num以后的元素
     *
     * @param Key   存储的key
     * @param value 存储的值
     * @param num   游标值
     * @return
     */
    public boolean setList(String Key, String value, long num) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            jedis.lpush(Key, value);
            // 删除游标num以后的元素
            List<String> end = null;
            end = jedis.lrange(Key, 0, -1);
            if (end.size() > num) {
                jedis.ltrim(Key, 0, num);
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 读取某个key的value 以List的形式
     *
     * @param key 存储的key
     * @return
     */
    public List<String> getList(String Key) {
        List<String> end = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();

            end = jedis.lrange(Key, 0, -1);
            return end;
        } catch (Exception e) {
            return end;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 读取某个key的value 以List的形式
     *
     * @param key 存储的key
     * @return
     */
    public List<String> getList(String Key, int s1, int e1) {
        List<String> end = null;
        Jedis jedis = null;
        try {
            jedis = getJedis();

            end = jedis.lrange(Key, s1, e1);
            return end;
        } catch (Exception e) {
            return end;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 根据key 删除缓存中某个value值(list)
     *
     * @param Key
     * @param value
     * @return
     */
    public String deleteListValue(String Key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            jedis.lrem(Key, 1, value);
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 如果该成员存在，则返回它的位置索引值(从低到高)。否则返回nil。
     *
     * @author 包智名
     */
    public long getIndexByKeyLowtoHigh(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zrank(key, member);
        } catch (Exception e) {
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 如果该成员存在，则返回它的位置索引值(从高到低)。否则返回nil。
     *
     * @author 包智名
     */
    public long getIndexByKeyHightoLow(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 返回Sorted-Sets中的成员数量，如果该Key不存在，返回0。
     *
     * @author 包智名
     */
    public long getSortedSetsCount(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zcard(key);
        } catch (Exception e) {
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取缓存中的map 包智名
     *
     * @param args
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Map<String, String> map = jedis.hgetAll(key);

            return map;
        } catch (Exception e) {
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 添加list by zf
     *
     * @param key
     * @param list
     * @param <T>
     */
    public <T> void setObjList(String key, List<T> list) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key.getBytes(), ObjectTranscoder.serialize(list));
        } catch (Exception e) {
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取list by zf
     *
     * @param <T>
     * @param key
     * @return list
     */
    public <T> List<T> getObjList(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis == null || !jedis.exists(key.getBytes())) {
                return null;
            }
            byte[] in = jedis.get(key.getBytes());
            @SuppressWarnings("unchecked")
            List<T> list = (List<T>) ObjectTranscoder.deserialize(in);
            return list;
        } catch (Exception e) {
        } finally {
            if (jedis != null) {
                int f = pool.getNumActive();
                jedis.close();
                int s = pool.getNumActive();
                log.info("poolNumActive释放前：" + f + "释放后：" + s);
            }
        }
        return null;
    }

    /**
     * 储存摸个Key和对象
     *
     * @param key   存储的key
     * @param value 存储的值
     * @return
     */
    public <T> boolean setObject(String key, T t) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(key.getBytes(), ObjectTranscoder.serialize(t));
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 返回一个对象
     *
     * @param string
     * @return
     */
    public Object getObject(String string) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return ObjectTranscoder.deserialize(jedis.get(string.getBytes()));
        } catch (Exception e) {
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 通过一个字节数组对应的key
     *
     * @param Key
     * @return
     */
    public boolean delObject(String Key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.del(Key.getBytes());
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    /**
     * 加锁
     * 
     * @param locaName 锁的key
     * @param acquireTimeout 获取超时时间
     * @param timeout 锁的超时时间
     * @return 锁标识
     */
    public String lockWithTimeout(String locaName, long acquireTimeout, long timeout) {
        Jedis conn = null;
        String retIdentifier = null;
        try {
            // 获取连接
            conn = getJedis();
            // 随机生成一个value
            String identifier = UUID.randomUUID().toString();
            // 锁名，即key值
            String lockKey = "lock:" + locaName;
            // 超时时间，上锁后超过此时间则自动释放锁
            int lockExpire = (int) (timeout / 1000);

            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + acquireTimeout;
            while (System.currentTimeMillis() < end) {
                if (conn.setnx(lockKey, identifier) == 1) {
                    conn.expire(lockKey, lockExpire);
                    // 返回value值，用于释放锁时间确认
                    retIdentifier = identifier;
                    return retIdentifier;
                }
                // 返回-1代表key没有设置超时时间，为key设置一个超时时间
                if (conn.ttl(lockKey) == -1) {
                    conn.expire(lockKey, lockExpire);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retIdentifier;
    }

    /**
     * 释放锁
     * 
     * @param lockName 锁的key
     * @param identifier 释放锁的标识
     * @return
     */
    public boolean releaseLock(String lockName, String identifier) {
        Jedis conn = null;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = getJedis();
            while (true) {
                // 监视lock，准备开始事务
                conn.watch(lockKey);
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                if (identifier.equals(conn.get(lockKey))) {
                    Transaction transaction = conn.multi();
                    transaction.del(lockKey);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        continue;
                    }
                    retFlag = true;
                }
                conn.unwatch();
                break;
            }
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return retFlag;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            MyRedis yibu = new MyRedis();
            List<String> list = new ArrayList<>();
            list.add("1");
            list.add("2");
            yibu.setObjList("isList", list);
            yibu.getObjList("isList");
            yibu.del("isList");
        }

		/*
         * yibu.del("Phone_065848F75DFA28EE5FDDF36DF74F4B0F");
		 * 
		 * //yibu.del("402881114a38fa00014a391c6d6f000b_content_mobile");
		 * HashMap<String,String> map =new HashMap<String,String>();
		 * map.put("name", "test1"); map.put("sex", "女"); map.put("order", "1");
		 * yibu.setHash("test1", map);
		 * 
		 * HashMap<String,String> map2 =new HashMap<String,String>();
		 * map2.put("name", "test2"); map2.put("sex", "女"); map2.put("order",
		 * "2"); yibu.setHash("test2", map2);
		 * 
		 * 
		 * HashMap<String,String> map4 =new HashMap<String,String>();
		 * map4.put("name", "test4"); map4.put("sex", "男"); map4.put("order",
		 * "4"); yibu.setHash("test4", map4);
		 * 
		 * 
		 * HashMap<String,String> map3 =new HashMap<String,String>();
		 * map3.put("name", "test3"); map3.put("sex", "女"); map3.put("order",
		 * "3"); yibu.setHash("test3", map3);
		 * 
		 * 
		 * HashMap<String,String> map5 =new HashMap<String,String>();
		 * map5.put("name", "test5"); map5.put("sex", "男"); map5.put("order",
		 * "5"); yibu.setHash("test5", map5);
		 * 
		 * 
		 * yibu.zadd("testSet", 1, "test1"); yibu.zadd("testSet", 2, "test2");
		 * yibu.zadd("testSet", 3, "test3"); yibu.zadd("testSet", 4, "test4");
		 * yibu.zadd("testSet", 5, "test5"); System.out.println(
		 * "===================插入完成==================================================="
		 * ); System.out.println(
		 * "===================从大到小循环==================================================="
		 * ); Set<Tuple> elements = yibu.zrevrangeWithScores("testSet", 0, -1);
		 * for(Tuple tuple: elements){ System.out.println(tuple.getElement() +
		 * "-" + tuple.getScore()); }
		 * 
		 * yibu.zadd("testSet", 1, "test5"); System.out.println(
		 * "===================从大到小循环完成==================================================="
		 * ); System.out.println(
		 * "===================从小到大循环==================================================="
		 * );
		 * 
		 * Set<Tuple> elements1 = yibu.zrangeWithScores("testSet", 0, -1);
		 * for(Tuple tuple: elements1){ System.out.println(tuple.getElement() +
		 * "-" + tuple.getScore()); System.out.println("性别：" +
		 * yibu.hget(tuple.getElement(), "sex")); } System.out.println(
		 * "===================从小到大循环完成==================================================="
		 * ); System.out.println(
		 * "===================删除test3==================================================="
		 * ); yibu.zrem("testSet","test3"); System.out.println(
		 * "===================删除test3完==================================================="
		 * ); System.out.println(
		 * "===================从小到大循环==================================================="
		 * );
		 * 
		 * Set<Tuple> elements2 = yibu.zrangeWithScores("testSet", 0, -1);
		 * for(Tuple tuple: elements2){ System.out.println(tuple.getElement() +
		 * "-" + tuple.getScore()); System.out.println("性别：" +
		 * yibu.hget(tuple.getElement(), "sex")); } System.out.println(
		 * "===================从小到大循环完成==================================================="
		 * ); yibu.set("qwy", "qwy123456");
		 */
    }

}
