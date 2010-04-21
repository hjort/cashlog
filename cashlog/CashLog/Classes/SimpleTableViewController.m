//
//  SimpleTableViewController.m
//  SimpleTable
//
//  Created by Daniel Poit on 02/02/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import "SimpleTableViewController.h"

@implementation SimpleTableViewController



/*
// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        // Custom initialization
    }
    return self;
}
*/

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/


/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.*/


- (void)viewDidLoad {
    [super viewDidLoad];

	NSMutableArray *paths = NSSearchPathForDirectoriesInDomains (NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex:0];	
	fullValorFile = [[NSString alloc] initWithFormat:@"%@/ValorFile", documentsDirectory];
	fullMemoFile = [[NSString alloc] initWithFormat:@"%@/MemoFile", documentsDirectory];
	
	arryData = [[NSMutableArray alloc] initWithContentsOfFile: fullMemoFile];
	valorText = [[NSMutableArray alloc] initWithContentsOfFile: fullValorFile];
	
	[arryData writeToFile: fullMemoFile atomically:NO];
	[valorText writeToFile: fullValorFile atomically:NO];

	oldValorFile = [[NSString alloc] initWithFormat:@"%@/oldValorFile", documentsDirectory];
	oldMemoFile = [[NSString alloc] initWithFormat:@"%@/oldMemoFile", documentsDirectory];	
	initBalanceFile = [[NSString alloc] initWithFormat:@"%@/BalanceFile", documentsDirectory];
	
	self.title =@"Cash Log";
	
	UIBarButtonItem *addButton = [[UIBarButtonItem alloc]
								  initWithTitle:@"Edit" style:UIBarButtonItemStyleBordered target:self action:@selector(EditTable:)];
	[self.navigationItem setLeftBarButtonItem:addButton animated:YES];
	
	EditingCell = -1;

	
//BEGIN BALANCE CALCULATION
	
	initBalance = [[NSString stringWithContentsOfFile:initBalanceFile] floatValue];
	lblInitBalance.text = [NSString stringWithFormat:@"Initial Balance  $ %3.2f", initBalance];
//	NSLog(@"Viewload initBalance=%3.2f", initBalance);
	
	int x=0;
	currBalance = initBalance;
	for (x; x<[valorText count]; x++){
		currBalance += [[[valorText objectAtIndex:x] substringFromIndex:2] floatValue];
	}
	lblCurrBalance.text = [NSString stringWithFormat:@"Current Balance  $ %3.2f", currBalance];
//	NSLog(@"Viewload currBalance=%3.2f - initBalance=%3.2f", currBalance, initBalance);
}


/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}


- (void)viewDidUnload {
		
}


- (void)dealloc {
    [super dealloc];
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 1;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	
	int count = [arryData count];
	return count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView
		cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	static NSString *CellIdentifier = @"Cell";
	
	UITableViewCell *cell = [tableView
							 dequeueReusableCellWithIdentifier:CellIdentifier];
	if (cell == nil) {
		cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle
									   reuseIdentifier:CellIdentifier] autorelease];
	}
		
	cell.text = [valorText objectAtIndex:indexPath.row];
	[[cell detailTextLabel] setText:[arryData objectAtIndex:indexPath.row]];
	
	cell.accessoryType = UITableViewCellAccessoryNone;
	
	return cell;
}

-(UITableViewCellEditingStyle)tableView:(UITableView *)aTableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	if (self.editing == NO || !indexPath) return
		UITableViewCellEditingStyleNone;
	
	return UITableViewCellEditingStyleDelete;
	
	return UITableViewCellEditingStyleNone;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	
	int Taman = [[valorText objectAtIndex:indexPath.row] length] - 3;

	txtValor.text = [NSString stringWithFormat:@"%3.0f", 
				[[[valorText objectAtIndex:indexPath.row] substringWithRange:NSMakeRange(3,Taman)] floatValue]*100];
	txtMemo.text = [arryData objectAtIndex:indexPath.row];
	
	EditingCell = indexPath.row;
	
	[cmdAdd setTitle:@"Change" forState:UIControlStateNormal];
}

- (void)tableView:(UITableView *)aTableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
	
	if (editingStyle == UITableViewCellEditingStyleDelete) {
		[arryData removeObjectAtIndex:indexPath.row];
		[valorText removeObjectAtIndex:indexPath.row];
		
		[tblSimpleTable reloadData];
				
		[arryData writeToFile: fullMemoFile atomically:NO];
		[valorText writeToFile: fullValorFile atomically:NO];	

//		BEGIN BALANCE CALCULATION
		
	//	NSLog(@"ItemDelete initBalance=%3.2f", initBalance);
		
		int x=0;
		currBalance = initBalance;
		for (x; x<[valorText count]; x++){
			currBalance += [[[valorText objectAtIndex:x] substringFromIndex:2] floatValue];
		}
		lblCurrBalance.text = [NSString stringWithFormat:@"Current Balance:  $ %3.2f", currBalance];
	//	NSLog(@"ItemDelete currBalance=%3.2f - initBalance=%3.2f", currBalance, initBalance);
		
	}
}

-(IBAction) mainAddButtonTouch {
	
//	NSLog(@"entrando AddMainButtonTouch - EditingCell-- value:%i", EditingCell);
	
	if(EditingCell != -1) {
		AddToCell = EditingCell;
	}
	else {
		AddToCell = [valorText count];
	}
			
	if(self.editing)
	{
		[super setEditing:NO animated:YES];
		[tblSimpleTable setEditing:NO animated:YES];
		
		initBalance = [txtInitBalance.text floatValue]/100;
		txtInitBalance.hidden = TRUE;
		lblInitBalance.text = [NSString stringWithFormat:@"Initial Balance  $ %3.2f", initBalance];
		[txtInitBalance resignFirstResponder];
		
		
		[self.navigationItem.leftBarButtonItem setTitle:@"Edit"];
		[self.navigationItem.leftBarButtonItem setStyle:UIBarButtonItemStylePlain];
		[self.navigationItem setRightBarButtonItem:nil animated:YES];
		
		[[NSString stringWithFormat:@"%3.2f", initBalance] writeToFile:initBalanceFile atomically:NO];
		[tblSimpleTable reloadData];

		int x=0;
		currBalance = initBalance;
		for (x; x<[valorText count]; x++){
			currBalance += [[[valorText objectAtIndex:x] substringFromIndex:2] floatValue];
		}
		lblCurrBalance.text = [NSString stringWithFormat:@"Current Balance:  $ %3.2f", currBalance];				
	}
	
	NSString *ValorSVirg = [[NSString alloc] initWithString:txtValor.text];

//	NSLog(@"Location do # =   %i", [ValorSVirg rangeOfString:@"#"].location);
	
	if ([ValorSVirg rangeOfString:@"+"].location != NSNotFound) {
		if ([ValorSVirg rangeOfString:@"+"].location > 0) {
			return;
		}
	}

	if ([ValorSVirg rangeOfString:@"*"].location != NSNotFound) {
		return;
	}
	
	if ([ValorSVirg rangeOfString:@"#"].location != NSNotFound) {
		return;
	}
	
	if ([ValorSVirg length] == 1){
		ValorSVirg=[NSString stringWithFormat:@"00%@", ValorSVirg];
	}
	if ([ValorSVirg length] == 0){
		return;
	}
		
	int Taman = [ValorSVirg length]-2;
	NSString *Cents = [ValorSVirg substringFromIndex:Taman];
	NSString *Dollars = [ValorSVirg substringToIndex:Taman];
	if ([Dollars isEqualToString:@""]){
		Dollars = @"0";
	}
	if ([Dollars isEqualToString:@"+"]){
		Dollars = @"+0";
	}
	
	NSString *ValorCVirg = [[NSString alloc] init];
	
	if ([[Dollars substringToIndex:1] isEqualToString:@"+"]) {
		ValorCVirg = [NSString stringWithFormat:@"$ %@.%@", Dollars, Cents];
	}
	else {
		ValorCVirg = [NSString stringWithFormat:@"$ -%@.%@", Dollars, Cents];
	}
	
	if (EditingCell != -1) {
		[valorText replaceObjectAtIndex:AddToCell withObject:ValorCVirg];
		[arryData replaceObjectAtIndex:AddToCell withObject:txtMemo.text];		
	}
	else {
		[valorText insertObject:ValorCVirg atIndex:AddToCell];
		[arryData insertObject:txtMemo.text atIndex:AddToCell];		
	}
	
	txtMemo.text = @"";
	txtValor.text = @"";
	
	[txtMemo resignFirstResponder];
	[txtValor resignFirstResponder];
			
	[tblSimpleTable reloadData];
		
	[arryData writeToFile: fullMemoFile atomically:NO];
	[valorText writeToFile: fullValorFile atomically:NO];
	
	[cmdAdd setTitle:@"Add" forState:UIControlStateNormal];
	EditingCell = -1;

//	BEGIN BALANCE CALCULATION
	
//	NSLog(@"mainAddButtonTouch initBalance=%3.2f", initBalance);
	
	int x=0;
	currBalance = initBalance;
	for (x; x<[valorText count]; x++){
		currBalance += [[[valorText objectAtIndex:x] substringFromIndex:2] floatValue];
	}
	lblCurrBalance.text = [NSString stringWithFormat:@"Current Balance:  $ %3.2f", currBalance];
//	NSLog(@"mainAddButtonTouch currBalance=%3.2f - initBalance=%3.2f", currBalance, initBalance);
	
}


-(IBAction) resetTable {
	
	UIBarButtonItem *undoButton = [[UIBarButtonItem alloc]
								  initWithTitle:@"Undo" style:UIBarButtonItemStyleDone target:self action:@selector(undoDelete)];
	[self.navigationItem setRightBarButtonItem:undoButton animated:YES];		
		
	[arryData writeToFile: oldMemoFile atomically:NO];
	[valorText writeToFile: oldValorFile atomically:NO];
	
	arryData = [[NSMutableArray alloc]
				initWithObjects: nil];
	valorText = [[NSMutableArray alloc]
				 initWithObjects: nil];
		
	[arryData writeToFile: fullMemoFile atomically:NO];
	[valorText writeToFile: fullValorFile atomically:NO];
	
	[tblSimpleTable reloadData];
	
//	BEGIN BALANCE CALCULATION
	
//	NSLog(@"resetTable initBalance=%3.2f", initBalance);
	
	txtInitBalance.text = [NSString stringWithFormat:@"%3.0f", currBalance * 100];
	oldBalance = initBalance;
	initBalance = currBalance;
	
	[[NSString stringWithFormat:@"%3.2f", initBalance] writeToFile:initBalanceFile atomically:NO];
//	NSLog(@"resetTable currBalance=%3.2f - initBalance=%3.2f - oldBalance=%3.2f", currBalance, initBalance, oldBalance);
}
	
-(IBAction) EditTable:(id)sender{
		
	if(self.editing)
	{
		[super setEditing:NO animated:YES];
		[tblSimpleTable setEditing:NO animated:YES];

		initBalance = [txtInitBalance.text floatValue]/100;
		txtInitBalance.hidden = TRUE;
		lblInitBalance.text = [NSString stringWithFormat:@"Initial Balance  $ %3.2f", initBalance];
		[txtInitBalance resignFirstResponder];

		[self.navigationItem.leftBarButtonItem setTitle:@"Edit"];
		[self.navigationItem.leftBarButtonItem setStyle:UIBarButtonItemStylePlain];
		[self.navigationItem setRightBarButtonItem:nil animated:YES];
		
		[[NSString stringWithFormat:@"%3.2f", initBalance] writeToFile:initBalanceFile atomically:NO];
		[tblSimpleTable reloadData];
		
		int x=0;
		currBalance = initBalance;
		for (x; x<[valorText count]; x++){
			currBalance += [[[valorText objectAtIndex:x] substringFromIndex:2] floatValue];
		}
		lblCurrBalance.text = [NSString stringWithFormat:@"Current Balance:  $ %3.2f", currBalance];		
	}
	else
	{
		[super setEditing:YES animated:YES];
		[tblSimpleTable setEditing:YES animated:YES];
		[tblSimpleTable reloadData];
		[self.navigationItem.leftBarButtonItem setTitle:@"Done"];
		[self.navigationItem.leftBarButtonItem setStyle:UIBarButtonItemStyleDone];
		UIBarButtonItem *addButton = [[UIBarButtonItem alloc]
									  initWithTitle:@"Reset" style:UIBarButtonItemStyleBordered target:self action:@selector(resetTable)];
		[self.navigationItem setRightBarButtonItem:addButton animated:YES];		
		[cmdAdd setTitle:@"Add" forState:UIControlStateNormal];
		EditingCell = -1;
		txtMemo.text = @"";
		txtValor.text = @"";
		txtInitBalance.hidden = FALSE;
		txtInitBalance.text = [NSString stringWithFormat:@"%3.0f", initBalance * 100];
	}	
}

-(IBAction) undoDelete {

	arryData = [[NSMutableArray alloc]
				initWithContentsOfFile:oldMemoFile];
	valorText = [[NSMutableArray alloc]
				 initWithContentsOfFile:oldValorFile];
	
	UIBarButtonItem *addButton = [[UIBarButtonItem alloc]
								  initWithTitle:@"Reset" style:UIBarButtonItemStyleBordered target:self action:@selector(resetTable)];
	[self.navigationItem setRightBarButtonItem:addButton animated:YES];	
	
	[tblSimpleTable reloadData];
	
	//	BEGIN BALANCE CALCULATION
	
//	NSLog(@"undoDelete initBalance=%3.2f", initBalance);
	
	initBalance = oldBalance;
	txtInitBalance.text = [NSString stringWithFormat:@"%3.0f", oldBalance * 100];
	
	int x=0;
	currBalance = initBalance;
	for (x; x<[valorText count]; x++){
		currBalance += [[[valorText objectAtIndex:x] substringFromIndex:2] floatValue];
	}

	lblCurrBalance.text = [NSString stringWithFormat:@"Current Balance  $ %3.2f", currBalance];	
	
	[[NSString stringWithFormat:@"%3.2f", initBalance] writeToFile:initBalanceFile atomically:NO];
//	NSLog(@"undoDelete currBalance=%3.2f - initBalance=%3.2f - oldBalance=%3.2f", currBalance, initBalance, oldBalance);
}

		
@end
