package com.grgbanking.ruralbank.main.model;

import java.util.List;

/**
 * Created by LiuPeng on 2016/8/4.
 * 供应商bean
 */
public class SupplierBean {
    /**
     * address :
     * [{"name":"北京","custId":"010","area":["东城区","西城区","崇文区"
     * ,"宣武区","延庆县"]},{"name"
     * :"上海","custId":"021","area":["黄浦区","卢湾区","徐汇区","长宁区","静安区","其他"]}]
     */

    /**
     * name : 北京 custId : 010 area : ["东城区","西城区","崇文区","宣武区","延庆县"]
     */

    private List<AddressEntity> lists;

    public void setAddress(List<AddressEntity> lists) {
        this.lists = lists;
    }

    public List<AddressEntity> getAddress() {
        return lists;
    }

    public static class AddressEntity {
        private String name;
        private String id;
        private List<SupListEntity> supLists;

        public void setName(String name) {
            this.name = name;
        }

        public void setCustId(String custId) {
            this.id = custId;
        }

        public void setArea(List<SupListEntity> area) {
            this.supLists = area;
        }

        public String getName() {
            return name;
        }

        public String getCustId() {
            return id;
        }

        public List<SupListEntity> getArea() {
            return supLists;
        }

        public static class SupListEntity {
            private String name;
            private String id;
            private String tell;
            private String address;
            private List<CustomerListEntity> customerList;

            public void setName(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getId() {
                return id;
            }

            public String getTell() {
                return tell;
            }

            public SupListEntity setTell(String tell) {
                this.tell = tell;
                return this;
            }

            public String getAddress() {
                return address;
            }

            public SupListEntity setAddress(String address) {
                this.address = address;
                return this;
            }

            public List<CustomerListEntity> getCustomerList() {
                return customerList;
            }

            public SupListEntity setCustomerList(List<CustomerListEntity> customerList) {
                this.customerList = customerList;
                return this;
            }

            public static class CustomerListEntity{
                private String name;
                private String id;
                private String phone;
                private String photo;
                private String roleName;
                private String supplierName;
                private String jobOrderNum;

                public String getName() {
                    return name;
                }

                public CustomerListEntity setName(String name) {
                    this.name = name;
                    return this;
                }

                public String getId() {
                    return id;
                }

                public CustomerListEntity setId(String id) {
                    this.id = id;
                    return this;
                }

                public String getPhone() {
                    return phone;
                }

                public CustomerListEntity setPhone(String phone) {
                    this.phone = phone;
                    return this;
                }

                public String getPhoto() {
                    return photo;
                }

                public CustomerListEntity setPhoto(String photo) {
                    this.photo = photo;
                    return this;
                }

                public String getRoleName() {
                    return roleName;
                }

                public CustomerListEntity setRoleName(String roleName) {
                    this.roleName = roleName;
                    return this;
                }

                public String getSupplierName() {
                    return supplierName;
                }

                public CustomerListEntity setSupplierName(String supplierName) {
                    this.supplierName = supplierName;
                    return this;
                }

                public String getJobOrderNum() {
                    return jobOrderNum;
                }

                public CustomerListEntity setJobOrderNum(String jobOrderNum) {
                    this.jobOrderNum = jobOrderNum;
                    return this;
                }
            }
        }
    }
}
