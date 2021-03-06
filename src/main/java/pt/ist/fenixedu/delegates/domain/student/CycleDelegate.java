/**
 * Copyright © 2011 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Delegates.
 *
 * FenixEdu Delegates is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Delegates is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Delegates.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.delegates.domain.student;

import static org.fenixedu.bennu.FenixEduDelegatesConfiguration.BUNDLE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.util.email.Recipient;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.UserGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;

import pt.ist.fenixedu.delegates.domain.accessControl.DelegateGroup;
import pt.ist.fenixedu.delegates.domain.util.email.DelegateSender;
import pt.ist.fenixedu.delegates.ui.DelegateBean;

public class CycleDelegate extends CycleDelegate_Base {

    public CycleDelegate(User user, Degree degree, CycleType cycle) {
        super();
        setUser(user);
        setDegree(degree);
        setCycle(cycle);
    }

    @Override
    public DelegateBean getBean() {
        return new DelegateBean(this);
    }

    private void setupRecipients() {
        getSender().addRecipients(Recipient.getRecipientFromGroup(DelegateGroup.get()));
        getSender().addRecipients(Recipient.getRecipientFromGroup(DelegateGroup.get(true)));
        getSender().addRecipients(Recipient.getRecipientFromGroup(DelegateGroup.get(getDegree())));
    }

    @Override
    public String getTitle() {
        String delegate = BundleUtil.getString(BUNDLE, "delegate");
        String of = BundleUtil.getString(BUNDLE, "delegate.of");
        return delegate + " " + of + " " + getCycle().getDescription();
    }

    @Override
    public Boolean samePosition(Delegate delegate) {
        CycleDelegate cycleDelegate = (CycleDelegate) delegate;
        if (getDegree().equals(cycleDelegate.getDegree()) && getCycle().equals(cycleDelegate.getCycle())) {
            return true;
        }
        return false;
    }

    @Override
    public List<CurricularCourse> getDelegateCourses() {
        List<CurricularCourse> toRet = new ArrayList<CurricularCourse>();
        ExecutionYear executionYearByDate = ExecutionYear.getExecutionYearByDate(getStart().toYearMonthDay());
        for (DegreeCurricularPlan curricularPlan : getDegree().getActiveDegreeCurricularPlans()) {
            for (ExecutionSemester execSem : executionYearByDate.getExecutionPeriodsSet()) {
                toRet.addAll(curricularPlan.getCycleCourseGroup(getCycle()).getAllCurricularCourses(execSem));
            }
        }
        return toRet.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public Boolean isDegreeOrCycleDelegate() {
        return true;
    }

    @Override
    public void setSender(DelegateSender sender) {
        super.setSender(sender);
        getSender().setMembers(UserGroup.of(getUser()));
        setupRecipients();
    }

    @Override
    public Boolean isYearDelegate() {
        return false;
    }

    @Override
    public CycleType getCycleType() {
        return getCycle();
    }

}
