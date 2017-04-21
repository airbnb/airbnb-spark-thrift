namespace java com.airbnb.spark.thrift

struct StructSimple {
    1: required i16 id16,
    2: required i32 id32,
    3: required i64 id64,
    4: required binary bin1,
    5: required bool b1,
    6: required double d1,
    7: required string str1,
    8: required list<i64> l1,
    9: required map<string, bool> m1,
    10: required set<double> s1,
    11: required Foo f1,
    12: required list<Foo> fooList,
    13: required map<string, Foo> fooMap,
    14: optional string option_str,
    15: required TestEnum e,
}

struct Foo {
    1: required i16 id16,
    2: required i32 id32,
    3: required i64 id64,
}

enum TestEnum {
    TWEET,
    RETWEET = 2,
    DM = 0xa,
    REPLY
}
