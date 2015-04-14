package org.storm.clickstream;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

/**
 * Container that holds requests made during a session.
 * 
 * @author Timothy Storm
 */
public class ClickStream implements Comparable<ClickStream>, Iterable<ClickStreamRequest>, Serializable {
    private static final long                          serialVersionUID = ClickStreamVersion.SVUID;

    private DateTime                                   _end;
    private final String                               _id;
    private final Queue<Reference<ClickStreamRequest>> _requests;
    private final DateTime                             _start;

    public ClickStream(final HttpSession session) {
        if (session == null) throw new IllegalArgumentException("session required");
        _id = session.getId();
        _start = new DateTime(DateTimeZone.UTC);
        _requests = new ArrayBlockingQueue<Reference<ClickStreamRequest>>(5_000_000);
    }

    public void addRequest(ClickStreamRequest request) {
        if (request == null) return;
        _requests.offer(new SoftReference<>(request));
    }

    @Override
    public int compareTo(ClickStream other) {
        if (other == null) return 1;
        if (other == this) return 0;

        CompareToBuilder compare = new CompareToBuilder();
        compare.append(getId(), other.getId());
        compare.append(getStart(), other.getStart());
        return compare.toComparison();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof ClickStream)) return false;
        if (obj == this) return true;

        ClickStream other = (ClickStream) obj;
        EqualsBuilder equals = new EqualsBuilder();
        equals.append(getId(), other.getId());
        equals.append(getStart(), other.getStart());
        return equals.isEquals();
    }

    public Duration getDuration() {
        if (_start == null || _end == null) return null;
        return new Duration(_start, _end);
    }

    public DateTime getEnd() {
        return _end;
    }

    public String getId() {
        return _id;
    }

    public List<ClickStreamRequest> getRequests() {
        List<ClickStreamRequest> requests = new ArrayList<>();
        Iterator<Reference<ClickStreamRequest>> refIt = null;

        for (refIt = _requests.iterator(); refIt.hasNext();) {
            Reference<ClickStreamRequest> ref = refIt.next();
            ClickStreamRequest r = ref.get();

            // remove gc'ed references
            if (r == null) refIt.remove();
            else requests.add(r);
        }
        return requests;
    }

    public DateTime getStart() {
        return _start;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder(17, 31);
        hash.append(getId());
        return hash.toHashCode();
    }

    @Override
    public Iterator<ClickStreamRequest> iterator() {
        return getRequests().iterator();
    }

    public void setEnd(DateTime end) {
        _end = end;
    }

    @Override
    public String toString() {
        ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        str.append("id", getId());
        str.append("start", getStart());
        str.append("end", getEnd());
        str.append("requests", getRequests().size());
        return str.toString();
    }
}
