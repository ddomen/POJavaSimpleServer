package DTO;

import java.util.Date;
import java.util.List;

public class DataSetMetadataDTO extends DTO {
    public String help;
    public Boolean success;
    public Result result;

    public class Result{
        public String creator_user_id;
        public List<Extra> extras;
        public List<Group> groups;
        public String id;
        public String license_id;
        public String license_url;
        public String log_message;
        public Date metadata_created;
        public Date metadata_modified;
        public String name;
        public String notes;
        public List<Organization> organization;
        public List<Resource> resources;
        public Date revision_timestamp;
        public String state;
        public String title;
        public String type;
        public String _catalog_parent_name;
        public String _catalog_source_url;

        public class Resource{
            public String created;
            public String description;
            public String format;
            public String id;
            public String last_modified;
            public String mimetype;
            public String name;
            public String resouce_group_id;
            public String revision_id;
            public String revision_timestamp;
            public String size;
            public String state;
            public String url;
        }

        public class Extra{
            public String key;
            public String value;
        }

        public class Group{
            public String description;
            public String display_name;
            public String id;
            public String name;
            public String title;
        }

        public class Organization{
            public Date created;
            public String description;
            public String id;
            public String image_url;
            public String name;
            public String title;
            public String type;
        }
    }
}
