
export const emptyAccount = {
    accountId: -1,
    username: '',
    firstname: '',
    lastname: '',
    fullName: '',
    balance: 0.0,
};

export const emptyBonus = {
    bonusId: -1,
    enabled: true,
    iconurl: '',
    title: '',
    description: '',
    bonusFactor: 1.0,
    startDate: new Date().toISOString(),
    endDate: new Date().toISOString(),
};
