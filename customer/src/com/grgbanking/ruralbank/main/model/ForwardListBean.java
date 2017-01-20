package com.grgbanking.ruralbank.main.model;

import java.util.List;

/**
 * 供应商对应的客服列表bean
 */
public class ForwardListBean {

//    private Objects lists;
//
//    public void setLists(Objects lists) {
//        this.lists = lists;
//    }
//
//    public Objects getLists() {
//        return lists;
//    }

    public static class ForwardListEntity {
        private List<ListEntity> lists;

        public void setLists(List<ListEntity> lists) {
            this.lists = lists;
        }

        public List<ListEntity> getLists() {
            return lists;
        }

        public static class ListEntity {
            private String name;
            private String id;
            private String phone;
            private String photo;
            private String roleName;
            private String supplierName;
            private String jobOrderNum;

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

            public String getPhone() {
                return phone;
            }

            public ListEntity setPhone(String phone) {
                this.phone = phone;
                return this;
            }

            public String getPhoto() {
                return photo;
            }

            public ListEntity setPhoto(String photo) {
                this.photo = photo;
                return this;
            }

            public String getRoleName() {
                return roleName;
            }

            public ListEntity setRoleName(String roleName) {
                this.roleName = roleName;
                return this;
            }

            public String getSupplierName() {
                return supplierName;
            }

            public ListEntity setSupplierName(String supplierName) {
                this.supplierName = supplierName;
                return this;
            }

            public String getJobOrderNum() {
                return jobOrderNum;
            }

            public ListEntity setJobOrderNum(String jobOrderNum) {
                this.jobOrderNum = jobOrderNum;
                return this;
            }
        }
    }
}
