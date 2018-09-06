namespace java com.airbnb.spark.thrift.generated.dummy
namespace py com.airbnb.spark.thrift.generated.dummy

enum EnumDummy {
    YES
    NO
    MAYBE
}

struct BasicDummy {
    1: required string reqStr
    2: optional string str
    3: optional i16 int16
    4: optional i32 int32
    5: optional i64 (js.type = "Long") int64
    6: optional double dbl
    7: optional byte byt
    8: optional bool bl
    9: optional binary bin
}

union UnionDummy {
    1: string str
    2: double dbl
}

union UnionRecursiveDummy {
    1: bool bl
    2: UnionRecursiveDummy ur
}

struct ComplexDummy {
    1: optional list<BasicDummy> bdList
    2: optional set<BasicDummy> bdSet
    3: optional map<string, BasicDummy> strToBdMap
    4: optional map<BasicDummy, string> bdToStrMap
    5: optional EnumDummy enumDummy
    6: optional UnionDummy unionDummy
    7: optional UnionRecursiveDummy unionRecursiveDummy
}
