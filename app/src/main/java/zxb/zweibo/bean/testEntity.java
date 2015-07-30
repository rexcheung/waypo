package zxb.zweibo.bean;

import com.sina.weibo.sdk.openapi.models.Geo;

import java.util.List;

/**
 * Created by rex on 15-7-30.
 */
public class testEntity {
    private String uve_blank;

    private String since_id;

    private String next_cursor;

    private String max_id;

    private String interval;

    private String has_unread;

    private String previous_cursor;

    private String total_number;

    private String[] ad;

    private String[] advertises;

    private Statuses[] statuses;

    private String hasvisible;

    public String getUve_blank() {
        return uve_blank;
    }

    public void setUve_blank(String uve_blank) {
        this.uve_blank = uve_blank;
    }

    public String getSince_id() {
        return since_id;
    }

    public void setSince_id(String since_id) {
        this.since_id = since_id;
    }

    public String getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(String next_cursor) {
        this.next_cursor = next_cursor;
    }

    public String getMax_id() {
        return max_id;
    }

    public void setMax_id(String max_id) {
        this.max_id = max_id;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getHas_unread() {
        return has_unread;
    }

    public void setHas_unread(String has_unread) {
        this.has_unread = has_unread;
    }

    public String getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(String previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public String getTotal_number() {
        return total_number;
    }

    public void setTotal_number(String total_number) {
        this.total_number = total_number;
    }

    public String[] getAd() {
        return ad;
    }

    public void setAd(String[] ad) {
        this.ad = ad;
    }

    public String[] getAdvertises() {
        return advertises;
    }

    public void setAdvertises(String[] advertises) {
        this.advertises = advertises;
    }

    public Statuses[] getStatuses() {
        return statuses;
    }

    public void setStatuses(Statuses[] statuses) {
        this.statuses = statuses;
    }

    public String getHasvisible() {
        return hasvisible;
    }

    public void setHasvisible(String hasvisible) {
        this.hasvisible = hasvisible;
    }

    @Override
    public String toString() {
        return "ClassPojo [uve_blank = " + uve_blank + ", since_id = " + since_id + ", next_cursor = " + next_cursor + ", max_id = " + max_id + ", interval = " + interval + ", has_unread = " + has_unread + ", previous_cursor = " + previous_cursor + ", total_number = " + total_number + ", ad = " + ad + ", advertises = " + advertises + ", statuses = " + statuses + ", hasvisible = " + hasvisible + "]";
    }



    class Statuses {
        private String comments_count;

        private retweeted_status retweeted_status;

        private PicUrls[] pic_urls;

        private String text;

        private Geo geo;

        private String source_allowclick;

        private Visible visible;

        private String attitudes_count;

        private String[] darwin_tags;

        private String in_reply_to_screen_name;

        private String mlevel;

        private String source_type;

        private String truncated;

        private String id;

        private String idstr;

        private String source;

        private String rid;

        private String favorited;

        private String in_reply_to_status_id;

        private String reposts_count;

        private String created_at;

        private String in_reply_to_user_id;

        private String mid;

        private User user;

        public String getComments_count() {
            return comments_count;
        }

        public void setComments_count(String comments_count) {
            this.comments_count = comments_count;
        }

        public retweeted_status getRetweeted_status() {
            return retweeted_status;
        }

        public void setRetweeted_status(retweeted_status retweeted_status) {
            this.retweeted_status = retweeted_status;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Geo getGeo() {
            return geo;
        }

        public void setGeo(Geo geo) {
            this.geo = geo;
        }

        public String getSource_allowclick() {
            return source_allowclick;
        }

        public void setSource_allowclick(String source_allowclick) {
            this.source_allowclick = source_allowclick;
        }

        public Visible getVisible() {
            return visible;
        }

        public void setVisible(Visible visible) {
            this.visible = visible;
        }

        public String getAttitudes_count() {
            return attitudes_count;
        }

        public void setAttitudes_count(String attitudes_count) {
            this.attitudes_count = attitudes_count;
        }

        public String[] getDarwin_tags() {
            return darwin_tags;
        }

        public void setDarwin_tags(String[] darwin_tags) {
            this.darwin_tags = darwin_tags;
        }

        public String getIn_reply_to_screen_name() {
            return in_reply_to_screen_name;
        }

        public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
            this.in_reply_to_screen_name = in_reply_to_screen_name;
        }

        public String getMlevel() {
            return mlevel;
        }

        public void setMlevel(String mlevel) {
            this.mlevel = mlevel;
        }

        public String getSource_type() {
            return source_type;
        }

        public void setSource_type(String source_type) {
            this.source_type = source_type;
        }

        public String getTruncated() {
            return truncated;
        }

        public void setTruncated(String truncated) {
            this.truncated = truncated;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIdstr() {
            return idstr;
        }

        public void setIdstr(String idstr) {
            this.idstr = idstr;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public String getFavorited() {
            return favorited;
        }

        public void setFavorited(String favorited) {
            this.favorited = favorited;
        }

        public String getIn_reply_to_status_id() {
            return in_reply_to_status_id;
        }

        public void setIn_reply_to_status_id(String in_reply_to_status_id) {
            this.in_reply_to_status_id = in_reply_to_status_id;
        }

        public String getReposts_count() {
            return reposts_count;
        }

        public void setReposts_count(String reposts_count) {
            this.reposts_count = reposts_count;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getIn_reply_to_user_id() {
            return in_reply_to_user_id;
        }

        public void setIn_reply_to_user_id(String in_reply_to_user_id) {
            this.in_reply_to_user_id = in_reply_to_user_id;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "ClassPojo [comments_count = " + comments_count + ", retweeted_status = " + retweeted_status + ", pic_urls = " + pic_urls + ", text = " + text + ", geo = " + geo + ", source_allowclick = " + source_allowclick + ", visible = " + visible + ", attitudes_count = " + attitudes_count + ", darwin_tags = " + darwin_tags + ", in_reply_to_screen_name = " + in_reply_to_screen_name + ", mlevel = " + mlevel + ", source_type = " + source_type + ", truncated = " + truncated + ", id = " + id + ", idstr = " + idstr + ", source = " + source + ", rid = " + rid + ", favorited = " + favorited + ", in_reply_to_status_id = " + in_reply_to_status_id + ", reposts_count = " + reposts_count + ", created_at = " + created_at + ", in_reply_to_user_id = " + in_reply_to_user_id + ", mid = " + mid + ", user = " + user + "]";
        }
    }

    class PicUrls {
        private String thumbnail_pic;

        public String getThumbnail_pic() {
            return thumbnail_pic;
        }

        public void setThumbnail_pic(String thumbnail_pic) {
            this.thumbnail_pic = thumbnail_pic;
        }
    }

    class User {

        private Long id;

        private String block_app;

        private String location;

        private String remark;

        private String verified_contact_email;

        private String verified_reason;

        private String statuses_count;

        private String city;

        private String favourites_count;

        private String idstr;

        private String description;

        private String verified;

        private String province;

        private String verified_contact_name;

        private String gender;

        private String weihao;

        private String cover_image;

        private String verified_reason_modified;

        private String mbrank;

        private String url;

        private String verified_level;

        private String verified_state;

        private String friends_count;

        private String profile_image_url;

        private String follow_me;

        private String ptype;

        private String verified_source_url;

        private String verified_type;

        private String verified_source;

        private String lang;

        private String verified_contact_mobile;

        private String credit_score;

        private String verified_trade;

        private String following;

        private String name;

        private String domain;

        private String created_at;

        private String user_ability;

        private String followers_count;

        private String online_status;

        private String profile_url;

        private String bi_followers_count;

        private String geo_enabled;

        private String star;

        private String urank;

        private String allow_all_comment;

        private String avatar_hd;

        private String allow_all_act_msg;

        private String avatar_large;

        private String pagefriends_count;

        private String verified_reason_url;

        private String mbtype;

        private String screen_name;

        private String block_word;

        public String getBlock_app() {
            return block_app;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setBlock_app(String block_app) {
            this.block_app = block_app;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getVerified_contact_email() {
            return verified_contact_email;
        }

        public void setVerified_contact_email(String verified_contact_email) {
            this.verified_contact_email = verified_contact_email;
        }

        public String getVerified_reason() {
            return verified_reason;
        }

        public void setVerified_reason(String verified_reason) {
            this.verified_reason = verified_reason;
        }

        public String getStatuses_count() {
            return statuses_count;
        }

        public void setStatuses_count(String statuses_count) {
            this.statuses_count = statuses_count;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getFavourites_count() {
            return favourites_count;
        }

        public void setFavourites_count(String favourites_count) {
            this.favourites_count = favourites_count;
        }

        public String getIdstr() {
            return idstr;
        }

        public void setIdstr(String idstr) {
            this.idstr = idstr;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVerified() {
            return verified;
        }

        public void setVerified(String verified) {
            this.verified = verified;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getVerified_contact_name() {
            return verified_contact_name;
        }

        public void setVerified_contact_name(String verified_contact_name) {
            this.verified_contact_name = verified_contact_name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getWeihao() {
            return weihao;
        }

        public void setWeihao(String weihao) {
            this.weihao = weihao;
        }

        public String getCover_image() {
            return cover_image;
        }

        public void setCover_image(String cover_image) {
            this.cover_image = cover_image;
        }

        public String getVerified_reason_modified() {
            return verified_reason_modified;
        }

        public void setVerified_reason_modified(String verified_reason_modified) {
            this.verified_reason_modified = verified_reason_modified;
        }

        public String getMbrank() {
            return mbrank;
        }

        public void setMbrank(String mbrank) {
            this.mbrank = mbrank;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVerified_level() {
            return verified_level;
        }

        public void setVerified_level(String verified_level) {
            this.verified_level = verified_level;
        }

        public String getVerified_state() {
            return verified_state;
        }

        public void setVerified_state(String verified_state) {
            this.verified_state = verified_state;
        }

        public String getFriends_count() {
            return friends_count;
        }

        public void setFriends_count(String friends_count) {
            this.friends_count = friends_count;
        }

        public String getProfile_image_url() {
            return profile_image_url;
        }

        public void setProfile_image_url(String profile_image_url) {
            this.profile_image_url = profile_image_url;
        }

        public String getFollow_me() {
            return follow_me;
        }

        public void setFollow_me(String follow_me) {
            this.follow_me = follow_me;
        }

        public String getPtype() {
            return ptype;
        }

        public void setPtype(String ptype) {
            this.ptype = ptype;
        }

        public String getVerified_source_url() {
            return verified_source_url;
        }

        public void setVerified_source_url(String verified_source_url) {
            this.verified_source_url = verified_source_url;
        }

        public String getVerified_type() {
            return verified_type;
        }

        public void setVerified_type(String verified_type) {
            this.verified_type = verified_type;
        }

        public String getVerified_source() {
            return verified_source;
        }

        public void setVerified_source(String verified_source) {
            this.verified_source = verified_source;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getVerified_contact_mobile() {
            return verified_contact_mobile;
        }

        public void setVerified_contact_mobile(String verified_contact_mobile) {
            this.verified_contact_mobile = verified_contact_mobile;
        }

        public String getCredit_score() {
            return credit_score;
        }

        public void setCredit_score(String credit_score) {
            this.credit_score = credit_score;
        }

        public String getVerified_trade() {
            return verified_trade;
        }

        public void setVerified_trade(String verified_trade) {
            this.verified_trade = verified_trade;
        }

        public String getFollowing() {
            return following;
        }

        public void setFollowing(String following) {
            this.following = following;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUser_ability() {
            return user_ability;
        }

        public void setUser_ability(String user_ability) {
            this.user_ability = user_ability;
        }

        public String getFollowers_count() {
            return followers_count;
        }

        public void setFollowers_count(String followers_count) {
            this.followers_count = followers_count;
        }

        public String getOnline_status() {
            return online_status;
        }

        public void setOnline_status(String online_status) {
            this.online_status = online_status;
        }

        public String getProfile_url() {
            return profile_url;
        }

        public void setProfile_url(String profile_url) {
            this.profile_url = profile_url;
        }

        public String getBi_followers_count() {
            return bi_followers_count;
        }

        public void setBi_followers_count(String bi_followers_count) {
            this.bi_followers_count = bi_followers_count;
        }

        public String getGeo_enabled() {
            return geo_enabled;
        }

        public void setGeo_enabled(String geo_enabled) {
            this.geo_enabled = geo_enabled;
        }

        public String getStar() {
            return star;
        }

        public void setStar(String star) {
            this.star = star;
        }

        public String getUrank() {
            return urank;
        }

        public void setUrank(String urank) {
            this.urank = urank;
        }

        public String getAllow_all_comment() {
            return allow_all_comment;
        }

        public void setAllow_all_comment(String allow_all_comment) {
            this.allow_all_comment = allow_all_comment;
        }

        public String getAvatar_hd() {
            return avatar_hd;
        }

        public void setAvatar_hd(String avatar_hd) {
            this.avatar_hd = avatar_hd;
        }

        public String getAllow_all_act_msg() {
            return allow_all_act_msg;
        }

        public void setAllow_all_act_msg(String allow_all_act_msg) {
            this.allow_all_act_msg = allow_all_act_msg;
        }

        public String getAvatar_large() {
            return avatar_large;
        }

        public void setAvatar_large(String avatar_large) {
            this.avatar_large = avatar_large;
        }

        public String getPagefriends_count() {
            return pagefriends_count;
        }

        public void setPagefriends_count(String pagefriends_count) {
            this.pagefriends_count = pagefriends_count;
        }

        public String getVerified_reason_url() {
            return verified_reason_url;
        }

        public void setVerified_reason_url(String verified_reason_url) {
            this.verified_reason_url = verified_reason_url;
        }

        public String getMbtype() {
            return mbtype;
        }

        public void setMbtype(String mbtype) {
            this.mbtype = mbtype;
        }

        public String getScreen_name() {
            return screen_name;
        }

        public void setScreen_name(String screen_name) {
            this.screen_name = screen_name;
        }

        public String getBlock_word() {
            return block_word;
        }

        public void setBlock_word(String block_word) {
            this.block_word = block_word;
        }
    }

    class retweeted_status{
        private String comments_count;

        private PicUrls[] pic_urls;

        private String text;

        private Geo geo;

        private String source_allowclick;

        private Visible visible;

        private String attitudes_count;

        private String[] darwin_tags;

        private String in_reply_to_screen_name;

        private String mlevel;

        private String source_type;

        private String truncated;

        private String bmiddle_pic;

        private String thumbnail_pic;

        private String id;

        private String idstr;

        private String source;

        private String original_pic;

        private String favorited;

        private String in_reply_to_status_id;

        private String reposts_count;

        private String created_at;

        private String in_reply_to_user_id;

        private String mid;

        private User user;

        public String getComments_count ()
        {
            return comments_count;
        }

        public void setComments_count (String comments_count)
        {
            this.comments_count = comments_count;
        }

        public PicUrls[] getPic_urls ()
        {
            return pic_urls;
        }

        public void setPic_urls (PicUrls[] pic_urls)
        {
            this.pic_urls = pic_urls;
        }

        public String getText ()
        {
            return text;
        }

        public void setText (String text)
        {
            this.text = text;
        }

        public Geo getGeo ()
        {
            return geo;
        }

        public void setGeo (Geo geo)
        {
            this.geo = geo;
        }

        public String getSource_allowclick ()
        {
            return source_allowclick;
        }

        public void setSource_allowclick (String source_allowclick)
        {
            this.source_allowclick = source_allowclick;
        }

        public Visible getVisible ()
        {
            return visible;
        }

        public void setVisible (Visible visible)
        {
            this.visible = visible;
        }

        public String getAttitudes_count ()
        {
            return attitudes_count;
        }

        public void setAttitudes_count (String attitudes_count)
        {
            this.attitudes_count = attitudes_count;
        }

        public String[] getDarwin_tags ()
        {
            return darwin_tags;
        }

        public void setDarwin_tags (String[] darwin_tags)
        {
            this.darwin_tags = darwin_tags;
        }

        public String getIn_reply_to_screen_name ()
        {
            return in_reply_to_screen_name;
        }

        public void setIn_reply_to_screen_name (String in_reply_to_screen_name)
        {
            this.in_reply_to_screen_name = in_reply_to_screen_name;
        }

        public String getMlevel ()
        {
            return mlevel;
        }

        public void setMlevel (String mlevel)
        {
            this.mlevel = mlevel;
        }

        public String getSource_type ()
        {
            return source_type;
        }

        public void setSource_type (String source_type)
        {
            this.source_type = source_type;
        }

        public String getTruncated ()
        {
            return truncated;
        }

        public void setTruncated (String truncated)
        {
            this.truncated = truncated;
        }

        public String getBmiddle_pic ()
        {
            return bmiddle_pic;
        }

        public void setBmiddle_pic (String bmiddle_pic)
        {
            this.bmiddle_pic = bmiddle_pic;
        }

        public String getThumbnail_pic ()
        {
            return thumbnail_pic;
        }

        public void setThumbnail_pic (String thumbnail_pic)
        {
            this.thumbnail_pic = thumbnail_pic;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getIdstr ()
        {
            return idstr;
        }

        public void setIdstr (String idstr)
        {
            this.idstr = idstr;
        }

        public String getSource ()
        {
            return source;
        }

        public void setSource (String source)
        {
            this.source = source;
        }

        public String getOriginal_pic ()
        {
            return original_pic;
        }

        public void setOriginal_pic (String original_pic)
        {
            this.original_pic = original_pic;
        }

        public String getFavorited ()
        {
            return favorited;
        }

        public void setFavorited (String favorited)
        {
            this.favorited = favorited;
        }

        public String getIn_reply_to_status_id ()
        {
            return in_reply_to_status_id;
        }

        public void setIn_reply_to_status_id (String in_reply_to_status_id)
        {
            this.in_reply_to_status_id = in_reply_to_status_id;
        }

        public String getReposts_count ()
        {
            return reposts_count;
        }

        public void setReposts_count (String reposts_count)
        {
            this.reposts_count = reposts_count;
        }

        public String getCreated_at ()
        {
            return created_at;
        }

        public void setCreated_at (String created_at)
        {
            this.created_at = created_at;
        }

        public String getIn_reply_to_user_id ()
        {
            return in_reply_to_user_id;
        }

        public void setIn_reply_to_user_id (String in_reply_to_user_id)
        {
            this.in_reply_to_user_id = in_reply_to_user_id;
        }

        public String getMid ()
        {
            return mid;
        }

        public void setMid (String mid)
        {
            this.mid = mid;
        }

        public User getUser ()
        {
            return user;
        }

        public void setUser (User user)
        {
            this.user = user;
        }
    }

    class Visible {

        /**
         * list_id : 0
         * type : 0
         */
        public int list_id;
        public int type;

        public int getList_id() {
            return list_id;
        }

        public void setList_id(int list_id) {
            this.list_id = list_id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}