package Dto;

import java.util.Date;
import java.util.List;

public class DtoPackage extends Dto {
    public String help;
    public Boolean success;
    public Result result;

    public class Result{
        public String creator_user_id;
        public List<DtoPackageExtra> extras;
        public List<DtoPackageGroup> groups;
        public String id;
        public String license_id;
        public String license_url;
        public String log_message;
        public Date metadata_created;
        public Date metadata_modified;
        public String name;
        public String notes;
        public List<DtoPackageOrganization> organization;
        public List<DtoPackageResource> resources;
        public Date revision_timestamp;
        public String state;
        public String title;
        public String type;
        public String _catalog_parent_name;
        public String _catalog_source_url;
    }
}