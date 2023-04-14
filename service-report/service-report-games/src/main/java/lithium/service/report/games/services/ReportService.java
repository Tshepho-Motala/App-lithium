package lithium.service.report.games.services;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lithium.service.report.games.data.entities.ReportAction;
import lithium.service.report.games.data.entities.ReportActionLabelValue;
import lithium.service.report.games.data.entities.ReportFilter;
import lithium.service.report.games.data.entities.ReportRevision;
import lithium.service.report.games.data.objects.ReportActionBasic;
import lithium.service.report.games.data.objects.ReportFilterBasic;
import lithium.service.report.games.data.repositories.ReportActionLabelValueRepository;
import lithium.service.report.games.data.repositories.ReportActionRepository;
import lithium.service.report.games.data.repositories.ReportFilterRepository;
import lithium.tokens.LithiumTokenUtil;

@Service
public class ReportService {
	@Autowired TokenStore tokenStore;
	@Autowired ReportFilterRepository reportFilterRepository;
	@Autowired ReportActionRepository reportActionRepository;
	@Autowired ReportActionLabelValueRepository reportActionLabelValueRepository;
	@Autowired ReportActionLabelValueService reportActionLabelValueService;
	
	public void copy(ReportRevision from, ReportRevision to, String updateBy) {
		to.setReport(from.getReport());
		to.setName(from.getName());
		to.setDescription(from.getDescription());
		to.setUpdateDate(new Date());
		to.setUpdateBy(updateBy);
		to.setNotifyEmail(from.getNotifyEmail());
		to.setGranularity(from.getGranularity());
		to.setGranularityOffset(from.getGranularityOffset());
		to.setCompareXperiods(from.getCompareXperiods());
		to.setAllFiltersApplicable(from.getAllFiltersApplicable());
		to.setCron(from.getCron());
	}
	
	public void copyFilters(ReportRevision from, ReportRevision to) {
		List<ReportFilter> filters = reportFilterRepository.findByReportRevision(from);
		filters.forEach(filter -> {
			reportFilterRepository.save(
				ReportFilter.builder()
				.reportRevision(to)
				.field(filter.getField())
				.operator(filter.getOperator())
				.value(filter.getValue())
				.build()
			);
		});
	}
	
	public void copyActionsAndActionLabelValues(ReportRevision from, ReportRevision to) {
		List<ReportAction> actions = reportActionRepository.findByReportRevision(from);
		actions.forEach(action -> {
			ReportAction editAction = reportActionRepository.save(
				ReportAction.builder()
				.reportRevision(to)
				.actionType(action.getActionType())
				.build()
			);
			action.getLabelValueList().forEach(lv -> {
				reportActionLabelValueService.findOrCreate(
					editAction, lv.getLabelValue().getLabel().getName(), lv.getLabelValue().getValue());
			});
		});
	}
	
	public void deleteFilters(List<ReportFilter> filters) {
		for (ReportFilter filter: filters) {
			reportFilterRepository.delete(filter);
		}
	}
	
	public void deleteActionsAndActionLabelValues(List<ReportAction> actions) {
		for (ReportAction action: actions) {
			for (ReportActionLabelValue ralv: action.getLabelValueList()) {
				reportActionLabelValueRepository.delete(ralv);
			}
			reportActionRepository.delete(action);
		}
	}
	
	public void addFilters(ReportRevision rev, ReportFilterBasic[] filters) {
		for (int i = 0; i < filters.length; i++) {
			ReportFilter reportFilter = ReportFilter.builder()
				.reportRevision(rev)
				.field(filters[i].getField())
				.operator(filters[i].getOperator())
				.value(filters[i].getValue())
				.build();
			reportFilter = reportFilterRepository.save(reportFilter);
		}
	}
	
	public void addActionsAndActionLabelValues(ReportRevision rev, ReportActionBasic[] actions) {
		for (int i = 0; i < actions.length; i++) {
			ReportAction reportAction = ReportAction.builder()
				.reportRevision(rev)
				.actionType(actions[i].getActionType())
				.build();
			reportAction = reportActionRepository.save(reportAction);
			if (actions[i].getRecipients().length > 0) {
				String label = "";
				if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)) {
					label = ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL;
				} else if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_REPORT_STATS_VIA_EMAIL)) {
					label = ReportActionService.LABEL_REPORT_STATS_RECIPIENT_EMAIL;
				}
				if (label != null && !label.isEmpty()) {
					for (String recipient: actions[i].getRecipients()) {
						reportActionLabelValueService.findOrCreate(reportAction, label, recipient);
					}
				}
			}
			if (actions[i].getEmailTemplate() != null &&
					!actions[i].getEmailTemplate().isEmpty()) {
				String label = "";
				if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)) {
					label = ReportActionService.LABEL_REPORT_FULL_EMAIL_TEMPLATE;
				} else if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_REPORT_STATS_VIA_EMAIL)) {
					label = ReportActionService.LABEL_REPORT_STATS_EMAIL_TEMPLATE;
				}
				if (label != null && !label.isEmpty()) {
					reportActionLabelValueService.findOrCreate(reportAction, label, actions[i].getEmailTemplate());
				}
			}
		}
	}
	
	public void checkPermission(String domainName, Principal principal) {
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		if (!tokenUtil.hasRole(domainName, "REPORT_GAMES"))
			throw new AccessDeniedException("User does not have access to reports for this domain");
	}
}